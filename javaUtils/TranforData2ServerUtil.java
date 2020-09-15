package com.alcatel.axs.app.importexport.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.alcatel.axs.applications.common.service.AppException;
import com.alcatel.axs.applications.common.service.RemoteFileManager;
import com.alcatel.axs.applications.common.service.RemoteFileUtil;
import com.alcatel.axs.applications.util.FileUtil;
import com.alcatel.axs.management.log.AmsLogger;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class TranforData2ServerUtil {
	private static AmsLogger m_logger = AmsLogger.getLogger(TranforData2ServerUtil.class);
	private static Set<String> m_binaryFileType = new HashSet<String>();

	static {
		m_binaryFileType.add(".gz");
	}

	public static void doFTPTransfer(File localFile, String remoteDir, String ipAddress, String username,
			String password) throws AppException {
		FTPClient client = new FTPClient();
		InetAddress address;
		FileInputStream is = null;
		List<File> newLocalFileList = new ArrayList<File>();
		try {
			address = InetAddress.getByName(ipAddress);
			client.connect(address);
			if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
				client.disconnect();
				m_logger.error(
						"FTP server refused connection, FTP server address: " + ipAddress + "username: " + username);
				throw new AppException("FTP server refused connection");
			}
			if (client.login(username, password)) {
				m_logger.fastdebug(
						"Starting FTP transfer..., FTP server address: " + ipAddress + "username: " + username);
				createDirectoryIfNotExist(client, remoteDir);
				closeStream(is);
				is = new FileInputStream(localFile);
				if (isUseFTPBinaryFileType(localFile.getName())) {
					m_logger.info("File transfer in BINARY file type");
					client.setFileType(FTP.BINARY_FILE_TYPE);
				} else {
					m_logger.info("File transfer in ASCII file type");
					client.setFileType(FTP.ASCII_FILE_TYPE);
				}
				boolean isSuccess = client.storeFile(FilenameUtils.getName(localFile.getAbsolutePath()), is);
				if (!isSuccess) {
					m_logger.error("File transfer failed. " + client.getReplyString());
					throw new AppException("File transfer failed. " + client.getReplyString());
				}
				client.logout();
				m_logger.info("FTP transfer completed");
			} else {
				m_logger.error("Unable to login to FTP server. Unknown username or password");
				throw new AppException("Unable to login to FTP server. Unknown username or password");
			}
		} catch (UnknownHostException e) {
			m_logger.error("UnknownHostException. FTP server address: " + ipAddress);
			throw new AppException("UnknownHostException. FTP server address: " + ipAddress);
		} catch (SocketException e) {
			throw new AppException(e.getMessage());
		} catch (IOException e) {
			throw new AppException("Exception in file transfer session. " + e.getMessage());
		} finally {
			// Delete all local new files
			for (File localNewFile : newLocalFileList) {
				localNewFile.delete();
			}

			if (client.isConnected()) {
				try {
					client.disconnect();
				} catch (IOException e) {

				}
			}
			closeStream(is);
		}
	}

	public static void doSFTPTransfer(File localFile, String remoteDir, String ipAddress, String username,
			String password) throws AppException {
		FileInputStream fStream = null;
		ChannelSftp channel = null;
		Session session = null;
		List<File> newLocalFileList = new ArrayList<File>();
		try {
			if (!RemoteFileManager.isRunningUnitTest()) {
				session = RemoteFileUtil.getSession(username, ipAddress, password);
			} else {
				session = RemoteFileUtil.getSession(ipAddress);
			}

			if (session != null && session.isConnected()) {
				channel = RemoteFileUtil.getSftpClient(session);
				m_logger.fastdebug("Create directory [%s] if not exist in host [%s]", remoteDir, session.getHost());
				createDirectoryIfNotExist(channel, remoteDir);
				m_logger.fastdebug("Starting a secure file transfer.......");
				closeStream(fStream);
				fStream = new FileInputStream(localFile);
				channel.put(fStream,
						remoteDir.concat(File.separator).concat(FilenameUtils.getName(localFile.getAbsolutePath())));
				m_logger.info("SFTP transfer completed");

			} else {
				throw new AppException("Cannot open SFTP Connection.");
			}
		} catch (SftpException | IOException e) {
			throw new AppException(e.getMessage());
		} finally {
			// Delete all new local files
			for (File newLocalFile : newLocalFileList) {
				newLocalFile.delete();
			}
			closeStream(fStream);
			RemoteFileUtil.close(session, channel);
		}
	}

	private static void closeStream(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (Exception e) {
			}
		}
	}

	private static void createDirectoryIfNotExist(FTPClient client, String remotePath) throws AppException {
		String path = remotePath;
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		String dirs[] = path.split(FileUtil.correctPathFile(File.separator));
		boolean dirExist = true;
		for (String dir : dirs) {

			if (dirExist) {
				try {
					dirExist = client.changeWorkingDirectory(dir);
				} catch (IOException e) {
					throw new AppException(e.getMessage());
				}
			}
			if (!dirExist) {
				try {
					if (!client.makeDirectory(dir)) {
						throw new AppException("Unable to create remote directory '" + client.printWorkingDirectory()
								+ File.separator + dir + "'.  error='" + client.getReplyString() + "'");
					}
					if (!client.changeWorkingDirectory(dir)) {
						throw new AppException("Unable to change into newly created remote directory '"
								+ client.printWorkingDirectory() + File.separator + dir + "'.  error='"
								+ client.getReplyString() + "'");
					}
				} catch (Exception e) {
					throw new AppException(e.getMessage());
				}
			}

		}
	}

	private static void createDirectoryIfNotExist(ChannelSftp channel, String remotePath)
			throws AppException, IOException {
		String path = remotePath;
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		String dirs[] = path.split(FileUtil.correctPathFile(File.separator));
		StringBuffer completeDir = new StringBuffer();
		for (int i = 0; i < dirs.length; i++) {
			completeDir.append(File.separator);
			completeDir.append(dirs[i]);
			m_logger.fastdebug("create dir: %s", completeDir.toString());
			try {
				channel.lstat(completeDir.toString());
			} catch (SftpException error) {
				if (error.getMessage().contains("No such file")) {
					try {
						channel.mkdir(completeDir.toString());
						channel.chmod(0777, remotePath);
					} catch (SftpException e) {
						m_logger.error("Error during create directory - " + completeDir, e);
						throw new IOException("Error during create directory " + e.getMessage());
					}
				} else {
					m_logger.error("Error during create directory - " + completeDir, error);
					throw new IOException("Error during create directory " + error.getMessage());
				}
			}
		}
	}

	private static boolean isUseFTPBinaryFileType(String fileName) {
		for (String s : m_binaryFileType) {
			if (fileName.endsWith(s)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private static List<String> getListSFTPFileName(String directory, ChannelSftp channel) throws IOException {
		List<String> listFileNames = new ArrayList<String>();
		try {
			Vector<LsEntry> directoryEntries = channel.ls(directory);
			Enumeration<LsEntry> elements = directoryEntries.elements();
			while (elements.hasMoreElements()) {
				LsEntry directoryEntry = (LsEntry) elements.nextElement();
				String fileName = directoryEntry.getFilename();
				if (!fileName.equals(".") && !fileName.equals("..")) {
					listFileNames.add(fileName);
				}
			}
		} catch (SftpException e) {
			m_logger.error("Error occurred while getting the file list from directory " + directory + " due to " + e);
			throw new IOException("Error occurred while getting the file list from directory " + directory, e);
		}
		return listFileNames;
	}

}

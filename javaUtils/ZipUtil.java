package com.alcatel.axs.app.importexport.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


public class ZipUtil {
    private static final Logger m_logger = Logger.getLogger(ZipUtil.class);


    public static boolean compressFiles2Zip(File file, File zipFile, boolean deleteFileAfterZip) {
    	m_logger.info("deleteFileAfterZip-----------------"+deleteFileAfterZip);
    	m_logger.info("file getAbsolutePath-----------------"+file.getAbsolutePath());
        InputStream inputStream = null;
        ZipArchiveOutputStream zipArchiveOutputStream = null;
        try {
            zipArchiveOutputStream = new ZipArchiveOutputStream(zipFile);
            //Use Zip64 extensions for all entries where they are required
            zipArchiveOutputStream.setUseZip64(Zip64Mode.AsNeeded);
            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, file.getName());
            zipArchiveOutputStream.putArchiveEntry(zipArchiveEntry);

            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 5];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                zipArchiveOutputStream.write(buffer, 0, len);
            }
            zipArchiveOutputStream.closeArchiveEntry();
            zipArchiveOutputStream.finish();

            if (deleteFileAfterZip) {
                file.deleteOnExit();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != zipArchiveOutputStream) {
                    zipArchiveOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    public static boolean decompressZip2Files(String zipFilePath, String targetDirPath) {

        InputStream inputStream = null;
        OutputStream outputStream = null;
        ZipArchiveInputStream zipArchiveInputStream = null;
        ArchiveEntry archiveEntry = null;
        try {
            File zipFile = new File(zipFilePath);
            inputStream = new FileInputStream(zipFile);
            zipArchiveInputStream = new ZipArchiveInputStream(inputStream, "UTF-8", false);

            while (null != (archiveEntry = zipArchiveInputStream.getNextEntry())) {
                String archiveEntryFileName = archiveEntry.getName();
                String archiveEntryPath = targetDirPath + archiveEntryFileName;
                File entryFile = new File(archiveEntryPath);
                if (!entryFile.exists()) {
                    boolean mkdirs = entryFile.getParentFile().mkdirs();

                }
                byte[] buffer = new byte[1024 * 5];
                outputStream = new FileOutputStream(entryFile);
                int len = -1;
                while ((len = zipArchiveInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
                if (null != zipArchiveInputStream) {
                    zipArchiveInputStream.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    public static File createTempFile(String fullFileName) throws IOException {
        String tempDirectoryPath = FileUtils.getTempDirectoryPath();
        File file = new File(tempDirectoryPath + File.separator + fullFileName);
        file.deleteOnExit();
        boolean newFile = file.createNewFile();
        m_logger.debug("newFile {"+fullFileName+"} => {"+newFile+"}");
        return file;
    }


}

import java.io.File;
class Delete{
	public static void main(String[] srgs){
		System.out.println("deleteǰ");
		File file = new File("Z:\\axs\\9.6.39_epg\\ssc.app\\inventory");
		Delete.deleteFile(file);
		System.out.println("delete��");
	}

	//ɾ���ļ����������ļ����ļ�
	public static void deleteFile(File file){
		File[] files = file.listFiles();
		if (files!=null) {
			for (File file2 : files) {
				if (file2.isFile()) {
					System.out.println(file2.getAbsolutePath());
					file2.delete();
				}else{
					deleteFile(file2);
				}
			}
		}
		System.out.println(file.getAbsolutePath());
		file.delete();
	}
//���պ�׺ɾ���ļ�
	public static void deleteFile2(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File file2 : files) {
                if (file2.isFile() && file2.getName().endsWith(".itheima")) {
                    System.out.println(file2.getAbsolutePath());
                    file2.delete();
                } else if (file2.isDirectory()){
                    deleteFile(file2);
                }
            }
        }
    }
	
	  // �����ļ���׺��
    public static void changeFileName(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File file2 : files) {
                if (file2.isFile() && file2.getName().endsWith(".rmvb")) {
                    String str = file2.toString();
                    System.out.println(str);
                    String str2 = str.substring(str.lastIndexOf("."));
                    str = str.replaceAll(str2, ".mp4");
                    System.out.println(str);
                    file2.renameTo(new File(str));
                }
            }
        }
    }
	
	public static void changeFileName2(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File file2 : files) {
                if (file2.isFile() && file2.getName().endsWith(".rmvb")) {
                    String str = file2.toString();
                    System.out.println(str);
                    String str2 = str.substring(str.lastIndexOf("."));
                    str = str.replaceAll(str2, ".mp4");
                    System.out.println(str);
                    file2.renameTo(new File(str));
                }
            }
        }
    }
}
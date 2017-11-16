package com.trade.eight.tools;

import android.content.Context;
import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * FileOperateHelper<br>
 * 文件操作类.
 */
public class FileOperator {
	
	public static final String ROOT_FOLDER_NAME = "APP";
	
	
	

    /**
     * 创建文件夹<br>
     * 若上级文件夹不存在则一并创建之.<br>
     * 
     * <pre><code>
     * pathName
     * </code>
     * 中涉及的文件路径分割符使用
     * <code>
     * File.separator
     * </code></pre>
     * 
     * @param pathName
     *            指定的文件夹路径.
     */
    public static void newFolder(String pathName) {
        try {
            File fileFolder = new File(pathName);
            if (!fileFolder.exists()) 
                fileFolder.mkdirs();
        } catch (Exception e) {
            System.out.println("*****Problem Creating new Folder*****"
                    + pathName + "*****");
            e.printStackTrace();
        }
    }

    /**
     * 在已经尊在的文件夹中创建指定名称的文件<br>
     * 
     * <pre><code>
     * pathName
     * </code>
     * 中涉及的文件路径分割符使用
     * <code>
     * File.separator
     * </code></pre>
     * 
     * @param pathToName
     *            文件夹路径及名称.
     * @param name
     *            需要被创建的文件名称.
     */
    public static void newFile(String pathToName, String name) {
        try {
            File file = new File(pathToName + File.separator + name);
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
        } catch (Exception e) {
            System.out.println("*****Problem Creating new File*****"
                    + pathToName + File.separator + name + "*****");
            e.printStackTrace();
        }
    }

    /**
     * 创建指定文件夹及其下指定名称的文件<br>
     * 
     * <pre><code>
     * pathName
     * </code>
     * 中涉及的文件路径分割符使用
     * <code>
     * File.separator
     * </code></pre>
     * 
     * 若上级文件夹不存在则一并创建之.
     * 
     * @param pathToName
     *            需要被创建的文件夹路径及名称.
     * @param name
     *            需要被创建的文件名称.
     */
    public static void newFolderAndFile(String pathToName, String name) {
        try {
            newFolder(pathToName);
            newFile(pathToName, name);
        } catch (Exception e) {
            System.out.println("*****Problem Creating new FolderAndFile*****"
                    + pathToName + File.separator + name + "*****");
            e.printStackTrace();
        }
    }

    /**
     * 将二进制流写入指定目录及名称的文件<br>
     * 调用该方法之前 目标文件必须已经存在(即使没有内容).
     * 
     * <pre><code>
     * pathName
     * </code>
     * 中涉及的文件路径分割符使用
     * <code>
     * File.separator
     * </code></pre>
     * 
     * @param input
     *            输入流.
     * @param filePathAndName
     *            指定的文件路径及名称.
     */
    public static void outBinaryFile(FileInputStream input,
            String filePathAndName) {
        try {
            FileOutputStream outFile = new FileOutputStream(filePathAndName);
            byte[] bytes = new byte[1024];
            int n;
            try {
                while ((n = input.read(bytes, 0, 1024)) != -1) 
                    outFile.write(bytes, 0, n);
                outFile.close();
                outFile = null;
                bytes = null;
            } catch (IOException e) {
                System.out
                        .println("*****Problem BinaryDataOut,Bytes Array*****");
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println("*****Problem BinaryDataOut,File "
                    + filePathAndName + "Not Found*****");
            e.printStackTrace();
        }
        try {
            input.close();
        } catch (IOException e) {
            System.out.println("*****Problem Close input*****");
            e.printStackTrace();
        }
    }
    
    public static File getJZrongRootFolder(Context context){
//    	String rootPath = Environment.getExternalStorageDirectory()
//				+ File.separator + ROOT_FOLDER_NAME;
//    	File file = new File(rootPath);
//    	if(!file.exists())
//    		file.mkdirs();
//    	return file;

        //modify by fangzhu , sdCard may be not exist
        return context.getCacheDir();
    }
    
    
    public static void deleteAllCache() {
        File newFile = new File(Environment.getExternalStorageDirectory() + ROOT_FOLDER_NAME);
        deleteCache(newFile);
    }
    
    private static void deleteCache(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
            if (file.isDirectory()) {
                final File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteCache(files[i]);
                }
            }
        }
    }

    
    
    
    public static final int IO_BUFFER_SIZE = 4 * 1024;

	public static File getExternalFile(String file) {
		return new File(Environment.getExternalStorageDirectory(), file);
	}

	/**
	 * Copy the content of the input stream into the output stream, using a
	 * temporary byte array buffer whose size is defined by
	 * {@link #IO_BUFFER_SIZE}.
	 * 
	 * @param in
	 *            The input stream to copy from.
	 * @param out
	 *            The output stream to copy to.
	 * 
	 * @throws java.io.IOException
	 *             If any error occurs during the copy.
	 */
	public void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[IO_BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	/**
	 * Closes the specified stream.
	 * 
	 * @param stream
	 *            The stream to close.
	 */
	public static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				android.util.Log.e("FileOperator", "Could not close stream", e);
			}
		}
	}
	
//	public static File getCacheWelPicFile() {
//		File file = getJZrongRootFolder() ;
//		return new File(file, "welcome.png");
//	}
	
	public static final String IMAGE_FOLDER_NAME = "image";
	
	public static File getImageFile(Context context) {
		File rootFolder = getJZrongRootFolder(context) ;
		File file = new File(rootFolder, IMAGE_FOLDER_NAME);
		if (!file.exists())
			file.mkdirs();
		return file;
	}
	
	public static File getImageByUrl(Context context, String imageUrl) {
		File folder = getImageFile(context);
		try {
			return new File(folder, imageUrl.substring(imageUrl
					.lastIndexOf("/") + 1));
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
}

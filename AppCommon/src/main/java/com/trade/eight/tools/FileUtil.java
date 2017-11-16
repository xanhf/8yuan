package com.trade.eight.tools;


import com.trade.eight.app.MyApplication;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FileUtil {

    static Comparator<File> comparator = new Comparator<File>() {
        public int compare(File f1, File f2) {
            if (f1 == null || f2 == null) {// 先比较null
                if (f1 == null) {
                    {
                        return -1;
                    }
                } else {
                    return 1;
                }
            } else {
                if (f1.isDirectory() == true && f2.isDirectory() == true) { // 再比较文件夹
                    return f1.getName().compareToIgnoreCase(f2.getName());
                } else {
                    if ((f1.isDirectory() && !f2.isDirectory()) == true) {
                        return -1;
                    } else if ((f2.isDirectory() && !f1.isDirectory()) == true) {
                        return 1;
                    } else {
                        return f1.getName().compareToIgnoreCase(f2.getName());// 最后比较文件
                    }
                }
            }
        }
    };
    private static String TAG = "FileUtil";

    public static void storageDefrag(File downloadDir, File[] taskFiles) {
        if (taskFiles == null || taskFiles.length == 0 || downloadDir == null
                || downloadDir.getAbsolutePath().endsWith("9999h")
                || downloadDir.getAbsolutePath().endsWith("9999h/")) {
            return;
        }
        try {
            FileFilter dirFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    // TODO Auto-generated method stub
                    if (pathname != null && pathname.isDirectory()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            File[] downloadFileDirs = downloadDir.listFiles(dirFilter);
            for (int i = 0; i < downloadFileDirs.length; i++) {
                String downloadFileDirName = downloadFileDirs[i].getName();
                if (!checkDownloadFileDir(taskFiles, downloadFileDirName)) {
                    FileUtil.deleteFile(downloadFileDirs[i].getAbsoluteFile());
                    // Log.v(TAG," THIS DownloadFileDir is " + downloadFileDirName);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static boolean checkDownloadFileDir(File[] taskFiles,
                                               String downloadFileDirName) {
        for (int j = 0; j < taskFiles.length; j++) {
            if (taskFiles[j].getName().startsWith(downloadFileDirName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return deleteFile(file);
    }

    public static boolean deleteFile(File file) {
        File[] files = file.listFiles();
        for (File deleteFile : files) {
            if (deleteFile.isDirectory()) {
                if (!deleteFile(deleteFile)) {
                    return false;
                }
            } else {
                if (!deleteFile.delete()) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    public static long getDirSize(String path) {
        if (path == null) {
            return 0;
        }
        File dir = new File(path);
        if (dir == null || !dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file.getAbsolutePath()); // 如果遇到目录则通过递归调用继续统计
            }
        }
        return dirSize;
    }

    public static File[] listSortedFiles(File dirFile) {
        assert dirFile.isDirectory();
        File[] files = dirFile.listFiles();

        File[] sortedFiles = new File[0];
        try {
            FileWrapper[] fileWrappers = new FileWrapper[files.length];
            for (int i = 0; i < files.length; i++) {
                fileWrappers[i] = new FileWrapper(files[i]);
            }

            Arrays.sort(fileWrappers);

            sortedFiles = new File[files.length];
            for (int i = 0; i < files.length; i++) {
                sortedFiles[i] = fileWrappers[i].getFile();
            }
        } catch (Exception e) {
            return files;
        }

        return sortedFiles;
    }

    public static boolean copyFolder(String source, String destinationn) {
        boolean flag = true;
        try {
            if (source == null || destinationn == null) return false;
            flag = copyFolder(new File(source), new File(destinationn));
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    public static boolean copyFolder(File source, File destinationn) {
        boolean flag = true;
        try {
            if (source == null || destinationn == null) return false;
            if (!source.exists()) {
                return false;
            }
            if (!destinationn.exists()) {
                destinationn.mkdirs();
            }
            File[] file = source.listFiles();
            FileInputStream fin = null;
            FileOutputStream fout = null;
            for (int i = 0; i < file.length; i++) {
                if (file[i].isFile()) {
                    try {
                        fin = new FileInputStream(file[i]);
                        File f = new File(destinationn + File.separator + file[i].getName());
                        fout = new FileOutputStream(f);
                        int c;
                        byte[] b = new byte[1024 * 5];
                        while ((c = fin.read(b)) != -1) {
                            fout.write(b, 0, c);
                        }
                    } catch (FileNotFoundException e) {
                        flag = false;
                        e.printStackTrace();
                        return false;
                    } catch (IOException e) {
                        flag = false;
                        e.printStackTrace();
                    } finally {
                        fin.close();
                        fout.flush();
                        fout.close();
                    }
                } else
                    copyFolder(new File(source + File.separator + file[i].getName()), new File(destinationn + File.separator + file[i].getName()));
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }


    public static boolean copyFile(File sourceFile, File desFolder) {
        boolean isok = true;
        if (!sourceFile.exists()) return false;
        isok = copyFile(sourceFile.getAbsolutePath(), desFolder.getAbsolutePath());
        return isok;
    }

    public static boolean copyFile(String oldPath, String folder) {
        boolean isok = true;
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            if (oldPath == null || folder == null) return false;
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newFolder = new File(folder);
            if (oldfile.exists()) {
                if (!newFolder.exists()) newFolder.mkdirs();
                inStream = new FileInputStream(oldPath);
                fs = new FileOutputStream(newFolder + File.separator + oldfile.getName());
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
            } else {
                isok = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isok = false;
        } finally {
            try {
                fs.flush();
                fs.close();
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isok;
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file
     */
    public static boolean deleteAllFile(File file) {
        try {
            if (file.isFile()) {
                return file.delete();
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    return file.delete();
                }
                for (File f : childFile) {
                    deleteAllFile(f);
                }
                return file.delete();
            }
        } catch (Exception e) {

        }
        return false;
    }

    public static boolean deleteAllFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return false;
        return deleteAllFile(file);
    }


    /**
     * get all file and folder list from path,
     *
     * @param path
     * @return
     */
    public static List<File> getFileList(String path) {
        List<File> dirList = new ArrayList<File>();
        try {
            if (path != null && path.length() > 0) {
                File currentFile = new File(path);
                if (!currentFile.exists()) return null;
                File[] files = currentFile.listFiles();
//                dirList = Arrays.asList(files);
                for (File f : files) {
                    dirList.add(f);
                }
                Collections.sort(dirList, comparator);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirList;
    }

    /**
     * get file or folder size
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSize(File f) {
        long size = 0;
        try {
            if (f.isFile()) return f.length();
            if (f.isDirectory()) {
                File flist[] = f.listFiles();
                if (flist == null || flist.length == 0) return 0;
                for (int i = 0; i < flist.length; i++) {
                    if (flist[i].isDirectory()) {
                        size = size + getFileSize(flist[i]);
                    } else {
                        size = size + flist[i].length();
                    }
                }
            }
        } catch (Exception e) {
            Log.v(TAG, "exception----");
            e.printStackTrace();
            return -1;
        } catch (Error e) {
            Log.v(TAG, "Error----");
            e.printStackTrace();
            return -1;
        }

        return size;
    }

    public static long getFileSize(String path) {
        try {
            if (path == null) return 0;
            return getFileSize(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean isAudioFile(File f) {
        try {
            if (f == null || !f.exists() || f.isDirectory()) return false;
            String fName = f.getName();
            int index = fName.lastIndexOf(".");
            String end = "";
            if (index == -1) {
                return false;
            } else {
                end = fName.substring(index + 1, fName.length()).toLowerCase();
            }
            if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                    || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    public static boolean isTxtFile(File f) {
        try {
            if (f == null || !f.exists() || f.isDirectory()) return false;
            String fName = f.getName();
            int index = fName.lastIndexOf(".");
            String end = "";
            if (index == -1) {
                return false;
            } else {
                end = fName.substring(index + 1, fName.length()).toLowerCase();
            }
            if (end.equals("txt")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    //    public static boolean isVideoFile (File f){
//        try {
//            if (f == null || !f.exists() || f.isDirectory() ) return false;
//            String fName = f.getName();
//            int index = fName.lastIndexOf(".");
//            String end = "";
//            if (index == -1) {
//                return false;
//            } else {
//                end = fName.substring(index + 1, fName.length()).toLowerCase();
//            }
//            if (BaseConstants.videotypeMap.containsKey(end)) {
//                return true;
//            }
//            return false;
//        }catch (Exception e) {
//            return false;
//        }
//
//    }
    public static boolean isImageFile(File f) {
        try {
            if (f == null || !f.exists() || f.isDirectory()) return false;
            String fName = f.getName();
            int index = fName.lastIndexOf(".");
            String end = "";
            if (index == -1) {
                return false;
            } else {
                end = fName.substring(index + 1, fName.length()).toLowerCase();
            }
            if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                    || end.equals("jpeg") || end.equals("bmp")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    public static boolean isApkFile(File f) {
        try {
            if (f == null || !f.exists() || f.isDirectory()) return false;
            String fName = f.getName();
            int index = fName.lastIndexOf(".");
            String end = "";
            if (index == -1) {
                return false;
            } else {
                end = fName.substring(index + 1, fName.length()).toLowerCase();
            }
            if (end.equals("apk")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    public static boolean renameFile(String source, String destination) {
        try {
            return new File(source).renameTo(new File(destination));
        } catch (Exception e) {

        }
        return false;
    }

    public static boolean renameFile(File source, File destination) {
        try {
            return source.renameTo(destination);
        } catch (Exception e) {

        }
        return false;
    }

    static class FileWrapper implements Comparable {
        private File file;

        public FileWrapper(File file) {
            this.file = file;
        }

        public int compareTo(Object obj) {
            assert obj instanceof FileWrapper;

            FileWrapper castObj = (FileWrapper) obj;

            if (this.file.getName().toLowerCase().compareTo(castObj.getFile().getName().toLowerCase()) > 0) {
                return 1;
            } else if (this.file.getName().toLowerCase().compareTo(castObj.getFile().getName().toLowerCase()) < 0) {
                return -1;
            } else {
                return 0;
            }
        }

        public File getFile() {
            return this.file;
        }
    }

    /**
     * 获取app的私有文件夹
     * @return
     */
    public static String getExternalFilePath() {
        String ss = MyApplication.getInstance().getExternalFilesDir(null).getPath();
        return ss;
    }

    /**
     * 获取缓存文件夹位置
     * @return
     */
    public static String getExternalCacheFile(){
        return MyApplication.getInstance().getExternalCacheDir().getAbsolutePath();
    }

}

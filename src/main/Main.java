import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("輸入資料夾名稱:");
        String srcDir = sc.nextLine();
        modifyMP3sAlbumToDirNameByDir(srcDir, "./output");
        File dir = new File("./src/test");
    }

    public static void modifyMP3sAlbumToDirNameByDir(String srcDir, String destDir) {
        srcDir = checkSrcDir(srcDir);
        File[] listOfFiles = getFolderFiles(srcDir);

        for(File file : listOfFiles) {
            if(isMP3(file)) {
                MP3File mp3 = new MP3File(file);
                String dirName = getDirName(srcDir);
                mp3.setAlbum_(dirName);
                mp3.save(destDir);
            }
        }
    }

    public static String checkSrcDir(String srcDir){
        if(srcDir.equals("")){
            srcDir = "./music";
        }
        return srcDir;
    }

    public static File[] getFolderFiles(String dirName) {
        File folder = new File(dirName);
        File[] listOfFiles = folder.listFiles();
        return listOfFiles;
    }

    public static boolean isMP3(File file) {
        if(file.isFile()) {
            if (getExtension(file).toUpperCase().equals("MP3")) {
                return true;
            }
        }
        return false;
    }

    public static String getExtension(File file) {
        if(!file.isFile()){
            return "dir";
        }
        int startIndex = file.getName().lastIndexOf(46) + 1;
        int endIndex = file.getName().length();
        return  file.getName().substring(startIndex, endIndex);
    }

    public static String getDirName(String srcDir){
        File file = new File(srcDir);
        return file.getName();
    }
}
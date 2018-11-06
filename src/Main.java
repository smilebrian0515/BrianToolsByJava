import java.io.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        modifyMP3sAlbumToDirNmaeByDir("./music");




    }

    public static void modifyMP3sAlbumToDirNmaeByDir(String dir)
    {
        File folder = new File("./music/");
        File[] listOfFiles = folder.listFiles();

        for(File file : listOfFiles)
        {
            if(file.isFile())
            {
                System.out.println(file.getName());
                if (getExtension(file).toUpperCase().equals("MP3"))
                {
                    System.out.println(file.getName() + " is MP3.");
                }
            }
        }


    }

    public static String getExtension(File file)
    {
        int startIndex = file.getName().lastIndexOf(46) + 1;
        int endIndex = file.getName().length();
        return  file.getName().substring(startIndex, endIndex);
    }

    public static byte[] getTAGBody ()
    {
        return new byte[0];
    }
}

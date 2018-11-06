import java.io.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        modifyMP3sAlbumToDirNameByDir("./music");

    }

    public static void modifyMP3sAlbumToDirNameByDir(String dir) {
        File[] listOfFiles = getFolderFiles("./music/");

        for(File file : listOfFiles) {
            if(isMP3(file)) {
                MP3File mp3 = new MP3File(file);
                System.out.println(mp3.getSongName());
                System.out.println(mp3.getArtist());
                System.out.println(mp3.getAlbum());
                System.out.println(mp3.getYear());
                System.out.println(mp3.getComment());
                System.out.println(mp3.getGenre());
            }
        }
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
        int startIndex = file.getName().lastIndexOf(46) + 1;
        int endIndex = file.getName().length();
        return  file.getName().substring(startIndex, endIndex);
    }
}

class MP3File {
    private byte[] TAGBody;
    private String Tag;
    private String SongName;
    private String Artist;
    private String Album;
    private String Year;
    private String Comment;
    private String Genre;
    private File file;

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getSongName() {
        return SongName;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String album) {
        Album = album;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public MP3File(File file) {
        try {
            this.file = file;
            TAGBody = getTAGBody(file);
            //因為Byte轉BIG5會有位元遺失，所以先把需要的段落複製出來，再轉換成String，得到需要的結果。
            Tag = new String(Arrays.copyOfRange(TAGBody,0,3),"BIG5").trim();
            SongName = new String(Arrays.copyOfRange(TAGBody,3,33),"BIG5").trim();
            Artist = new String(Arrays.copyOfRange(TAGBody,33,63),"BIG5").trim();
            Album = new String(Arrays.copyOfRange(TAGBody,63,93),"BIG5").trim();
            Year = new String(Arrays.copyOfRange(TAGBody,93,97),"BIG5").trim();
            Comment = new String(Arrays.copyOfRange(TAGBody,97,127),"BIG5").trim();
            Genre = new String(Arrays.copyOfRange(TAGBody,127,128),"BIG5").trim();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private byte[] getTAGBody(File file) throws Exception{
        byte[] tagBody = new byte[128];
        FileInputStream fs = new FileInputStream(file);
        fs.skip(fs.available() - 128);
        fs.read(tagBody);
        fs.close();
        return tagBody;
    }



}

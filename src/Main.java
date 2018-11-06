import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
                mp3.setAlbum("阿誠");
                mp3.save();
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
    //TAGBody 是 MP3 預留的最後128個byte，詳細資訊
    private byte[] TAGBody;
    private String Tag;
    private String SongName;
    private String Artist;
    private String Album;
    private String Year;
    private String Comment;
    private String Genre;
    private File file;

    private static final int Tag_Start = 0;
    private static final int Tag_End = 3;
    private static final int SongName_Start = 3;
    private static final int SongName_End = 33;
    private static final int Artist_Start = 33;
    private static final int Artist_End = 63;
    private static final int Album_Start = 63;
    private static final int Album_End = 93;
    private static final int Year_Start = 93;
    private static final int Year_End = 97;
    private static final int Comment_Start = 97;
    private static final int Comment_End = 127;
    private static final int Genre_Start = 127;
    private static final int Genre_End = 128;

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
            Tag = new String(Arrays.copyOfRange(TAGBody,Tag_Start,Tag_End),"BIG5").trim();
            SongName = new String(Arrays.copyOfRange(TAGBody,SongName_Start,SongName_End),"BIG5").trim();
            Artist = new String(Arrays.copyOfRange(TAGBody,Artist_Start,Artist_End),"BIG5").trim();
            Album = new String(Arrays.copyOfRange(TAGBody,Album_Start,Album_End),"BIG5").trim();
            Year = new String(Arrays.copyOfRange(TAGBody,Year_Start,Year_End),"BIG5").trim();
            Comment = new String(Arrays.copyOfRange(TAGBody,Comment_Start,Comment_End),"BIG5").trim();
            Genre = new String(Arrays.copyOfRange(TAGBody,Genre_Start,Genre_End),"BIG5").trim();
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

    public void save(){
        try{
            modifyTAGBody();
            writeFile();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    private void modifyTAGBody() throws Exception{
        writeTagValueToTAGBody(SongName, SongName_Start,SongName_End);
        writeTagValueToTAGBody(Artist, Artist_Start,Artist_End);
        writeTagValueToTAGBody(SongName, SongName_Start,SongName_End);
        writeTagValueToTAGBody(Album, Album_Start,Album_End);
        writeTagValueToTAGBody(Year, Year_Start,Year_End);
        writeTagValueToTAGBody(Comment, Comment_Start,Comment_End);
        writeTagValueToTAGBody(Genre, Genre_Start,Genre_End);
    }

    private void writeTagValueToTAGBody(String TAG, int startIndex, int EndIndex) throws Exception {
        byte[] b = TAG.getBytes(Charset.forName("BIG5"));
        if(b.length > (EndIndex-startIndex)){
            throw new Exception("MP3Value is too long to save.");
        }else{
            int i = 0;
            while(startIndex < EndIndex){
                if(i<b.length){
                    TAGBody[startIndex++] = b[i];
                    i++;
                }else{
                    TAGBody[startIndex++] = 0;
                }
            }
        }
    }

    public void writeFile()throws Exception{
        InputStream is = null;
        OutputStream os = null;
        try{
            is = new FileInputStream(file);
            os = new FileOutputStream(new File(file.getParent()+"\\..\\output\\"+file.getName()));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0){
                //MP3的最後128byte是TAGBody，要算好會不會斷掉。
                if(is.available()<128 && is.available() > 0){ //取代前半段
                    buffer=TAGBodyReplacePartOfBuffer(buffer, length,128-is.available());
                    System.out.println("1");
                }else{
                    if(length < 1024){
                        if(length >  128){ //取代整個
                            buffer=TAGBodyReplacePartOfBuffer(buffer, length,128);
                            System.out.println("2");
                        }else{ //取代後半段
                            buffer=TAGBodyReplacePartOfBuffer(buffer, length,length);
                            System.out.println("3");
                        }
                    }
                }

                os.write(buffer,0,length);
            }
        }finally{
            is.close();
            os.close();
        }
    }

    private byte[] TAGBodyReplacePartOfBuffer(byte[] buffer,int bufferLength, int replaceLength){
        int j = 0;
        if(buffer.length == replaceLength){
            j = 128 - replaceLength;
        }
        for(int i=bufferLength-replaceLength;i<bufferLength;i++){
            buffer[i] = TAGBody[j++];
        }
        return buffer;
    }
}

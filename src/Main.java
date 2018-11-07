import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        modifyMP3sAlbumToDirNameByDir("./music", "./output");

    }

    public static void modifyMP3sAlbumToDirNameByDir(String srcDir, String destDir) {
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

    public static String getDirName(String srcDir){
        File file = new File(srcDir);
        return file.getName();
    }
}

class MP3File {
    //TAGBody 是 MP3 預留的最後128個byte，詳細資訊
    private byte[] TAGBody;
    private String Tag_;
    private String SongName_;
    private String Artist_;
    private String Album_;
    private String Year_;
    private String Comment_;
    private String Genre_;
    private File file_;

    private static final int TAG_START = 0;
    private static final int TAG_END = 3;
    private static final int SONG_NAME_START = 3;
    private static final int SONG_NAME_END = 33;
    private static final int ARTIST_START = 33;
    private static final int ARTIST_END = 63;
    private static final int ALBUM_START = 63;
    private static final int ALBUM_END = 93;
    private static final int YEAR_START = 93;
    private static final int YEAR_END = 97;
    private static final int COMMENT_START = 97;
    private static final int COMMENT_END = 127;
    private static final int GENRE_START = 127;
    private static final int GENRE_END = 128;

    public String getTag_() {
        return Tag_;
    }

    public void setTag_(String tag_) {
        Tag_ = tag_;
    }

    public String getSongName_() {
        return SongName_;
    }

    public void setSongName_(String songName_) {
        SongName_ = songName_;
    }

    public String getArtist_() {
        return Artist_;
    }

    public void setArtist_(String artist_) {
        Artist_ = artist_;
    }

    public String getAlbum_() {
        return Album_;
    }

    public void setAlbum_(String album_) {
        Album_ = album_;
    }

    public String getYear_() {
        return Year_;
    }

    public void setYear_(String year_) {
        Year_ = year_;
    }

    public String getComment_() {
        return Comment_;
    }

    public void setComment_(String comment_) {
        Comment_ = comment_;
    }

    public String getGenre_() {
        return Genre_;
    }

    public void setGenre_(String genre_) {
        Genre_ = genre_;
    }

    public MP3File(File file) {
        try {
            this.file_ = file;
            TAGBody = getTAGBody(file);
            //因為Byte轉BIG5會有位元遺失，所以先把需要的段落複製出來，再轉換成String，得到需要的結果。
            Tag_ = new String(Arrays.copyOfRange(TAGBody, TAG_START, TAG_END),"BIG5").trim();
            SongName_ = new String(Arrays.copyOfRange(TAGBody, SONG_NAME_START, SONG_NAME_END),"BIG5").trim();
            Artist_ = new String(Arrays.copyOfRange(TAGBody, ARTIST_START, ARTIST_END),"BIG5").trim();
            Album_ = new String(Arrays.copyOfRange(TAGBody, ALBUM_START, ALBUM_END),"BIG5").trim();
            Year_ = new String(Arrays.copyOfRange(TAGBody, YEAR_START, YEAR_END),"BIG5").trim();
            Comment_ = new String(Arrays.copyOfRange(TAGBody, COMMENT_START, COMMENT_END),"BIG5").trim();
            Genre_ = new String(Arrays.copyOfRange(TAGBody, GENRE_START, GENRE_END),"BIG5").trim();
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

    public void save(String destDir){
        try{
            modifyTAGBody();
            writeFile(destDir);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    private void modifyTAGBody() throws Exception{
        writeTagValueToTAGBody(SongName_, SONG_NAME_START, SONG_NAME_END);
        writeTagValueToTAGBody(Artist_, ARTIST_START, ARTIST_END);
        writeTagValueToTAGBody(SongName_, SONG_NAME_START, SONG_NAME_END);
        writeTagValueToTAGBody(Album_, ALBUM_START, ALBUM_END);
        writeTagValueToTAGBody(Year_, YEAR_START, YEAR_END);
        writeTagValueToTAGBody(Comment_, COMMENT_START, COMMENT_END);
        writeTagValueToTAGBody(Genre_, GENRE_START, GENRE_END);
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

    public void writeFile(String destDir)throws Exception{
        InputStream is = null;
        OutputStream os = null;
        try{
            is = new FileInputStream(file_);
            os = new FileOutputStream(new File(destDir+"\\"+ file_.getName()));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0){
                //MP3的最後128byte是TAGBody，要算好會不會斷掉。
                if(is.available()<128 && is.available() > 0){ //取代前半段
                    buffer=TAGBodyReplacePartOfBuffer(buffer, length,128-is.available());
                }else{
                    if(length < 1024){
                        if(length >  128){ //取代整個
                            buffer=TAGBodyReplacePartOfBuffer(buffer, length,128);
                        }else{ //取代後半段
                            buffer=TAGBodyReplacePartOfBuffer(buffer, length,length);
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

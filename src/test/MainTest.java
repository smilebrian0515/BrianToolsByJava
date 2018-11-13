import org.junit.*;

import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.*;

import java.io.File;

import static org.junit.Assert.*;

public class MainTest {
    @BeforeClass
    public static void beforeClass(){
        System.out.println("BC");
    }
    @Before
    public void before(){
        System.out.println("B");
    }
    @After
    public void after(){
        System.out.println("A");
    }
    @AfterClass
    public static void afterClass(){
        System.out.println("AC");
    }
    @Test
    public void Case1() {
        String str= "Junit is working fine";
        assertEquals("Junit is working fine",str);
    }

    @Test
    public void Case2() {
        System.out.println("Hello world");
    }

    @Test
    public void getDirNameTest(){
        // 1. Arrange
        Main m = new Main();
        String src = "./music";
        String excepted = "music";

        // 2. Act
        String actual = m.getDirName(src);

        // 3. Assert
        assertThat(actual).isEqualTo(excepted);
    }

    @Test
    public void Case4(){
        int actual1 = 5;
        String actual2 = "10";

        SoftAssertions softAssertions = new SoftAssertions();

        softAssertions.assertThat(actual1).isLessThan(4);

        softAssertions.assertThat(actual2).isEqualTo("11");

        //softAssertions.assertAll();
    }

    @Test
    public void Case5(){
        Main m = new Main();
        File file = new File("./src/test/SongForTest.mp3");

        boolean actual = m.isMP3(file);

        assertThat(actual).isTrue();
    }

    @Test
    public void Case6(){
        Main m = new Main();
        File dir = new File("./src/test");
        File file = new File("./src/test/SongForTest.mp3");

        String actual1 = m.getExtension(dir);
        String actual2 = m.getExtension(file);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual1).isEqualTo("dir");
        softAssertions.assertThat(actual2).isEqualTo("mp3");
        softAssertions.assertAll();
    }
    @Test
    public void Case7(){

    }
    @Test
    public void Case8(){

    }
    @Test
    public void Case9(){

    }
    @Test
    public void Case10(){

    }
}
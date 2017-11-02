package test;

/**
 * Created by lianbin.wang on 11/4/16.
 */
public class LocalTest {

    @org.junit.Test
    public void testClasspath() {
        System.out.println(System.getenv("classpath"));
        System.out.println(System.getProperty("classpath"));
        System.out.println(System.getenv("java.class.path"));
        System.out.println(System.getProperty("java.class.path"));
    }

}

import general.Read;
import general.ReadResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public class ReadTest {

    private static final File file = new File("src\\test\\java\\test.log");
    private Read read;

    @BeforeAll
    public static void beforeAll() {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void beforeEach() {
        read = new Read(file);
    }

    @AfterEach
    public void afterEach() {
        GeneralFileActions.clean(file);
    }

    @AfterAll
    public static void afterAll() {
        file.delete();
    }

    @ParameterizedTest
    @MethodSource("readTestArguments")
    public void readTest(String info, ReadResult excepted) {
        GeneralFileActions.write(file, info);
        Assertions.assertEquals(excepted, read.read());
    }

    public static Stream<Arguments> readTestArguments() {
        String ip1 = "localhost";
        String ip2 = "121.122.12.12";
        int port1 = 8080;
        int port2 = 2020;
        int port3 = 1090;
        int port4 = 8888;
        int port5 = 8088;
        int port6 = 9888;
        return Stream.of(Arguments.of(ip1 + " " + port1, new ReadResult(ip1, port1)),
                Arguments.of(ip1 + " " + port2, new ReadResult(ip1, port2)),
                Arguments.of(ip1 + " " + port3, new ReadResult(ip1, port3)),
                Arguments.of(ip2 + " " + port4, new ReadResult(ip2, port4)),
                Arguments.of(ip2 + " " + port5, new ReadResult(ip2, port5)),
                Arguments.of(ip2 + " " + port6, new ReadResult(ip2, port6)));
    }
}

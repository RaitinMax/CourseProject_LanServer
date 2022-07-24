import general.Log;
import general.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class LoggerTest {

    private static final File file = new File("src\\test\\java\\test.log");
    private Log log;

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
        log = new Logger(file);
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
    @MethodSource("oneLogTestArguments")
    public void oneLogTest(String logMessage) {
        log.log(logMessage);
        Assertions.assertEquals(logMessage, GeneralFileActions.read(file));
    }

    @ParameterizedTest
    @MethodSource("lotsLogTestArguments")
    public void lotsLogTest(List<String> logs, String excepted) {
        logs.forEach(log::log);
        Assertions.assertEquals(excepted, GeneralFileActions.read(file));
    }

    public static Stream<String> oneLogTestArguments() {
        String message1 = "Всем привет!";
        String message2 = "Тест";
        String message3 = "Ошибка! Причина: Потеря соединения с Интернетом";
        String message4 = "Успешно!";
        return Stream.of(message1, message2, message3, message4);
    }

    public static Stream<Arguments> lotsLogTestArguments() {
        String message1 = "Привет!";
        String message2 = "Тестирую...";
        String message3 = "Неизвестная ошибка!";
        String message4 = "Успешно отправлено сообщение!";
        return Stream.of(Arguments.of(Arrays.asList(message1, message2), message1 + message2),
                Arguments.of(Arrays.asList(message3, message4),  message3 + message4));
    }
}

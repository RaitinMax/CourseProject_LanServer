import general.Read;
import general.ReadResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import server.Server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class ServerTest {

    private static final File file = new File("config\\server\\settings.txt");
    private static final ReadResult result = new Read(file).read();
    private ExecutorService executorService;
    private static final Server server = new Server(msg -> {});

    @BeforeAll
    public static void beforeAll() {
        new Thread(() -> server.start(result.getIp(), result.getPort())).start();
    }

    @BeforeEach
    public void beforeEach() {
        executorService = Executors.newFixedThreadPool(4);
    }

    @AfterEach
    public void afterEach() {
        executorService.shutdown();
    }

    @AfterAll
    public static void afterAll() {
        server.stop();
    }

    @ParameterizedTest
    @MethodSource("testOneMessageTwoTreadArguments")
    public void testOneMessageTwoTreads(String message1, String message2) throws IOException, ExecutionException, InterruptedException {
        try (SocketChannel client1 = SocketChannel.open();
             SocketChannel client2 = SocketChannel.open()) {
            client1.connect(new InetSocketAddress(result.getIp(), result.getPort()));
            ByteBuffer buffer1 = ByteBuffer.allocate(2 << 10);
            client2.connect(new InetSocketAddress(result.getIp(), result.getPort()));
            ByteBuffer buffer2 = ByteBuffer.allocate(2 << 10);
            Future<String> future1 = executorService.submit(() -> GeneralNIOActions.readOneMessage(client1, buffer1));
            Future<String> future2 = executorService.submit(() -> GeneralNIOActions.readOneMessage(client2, buffer2));
            executorService.execute(() -> GeneralNIOActions.writeOneMessage(message1, client1));
            executorService.execute(() -> GeneralNIOActions.writeOneMessage(message2, client2));
            Assertions.assertEquals(message1, future2.get());
            Assertions.assertEquals(message2, future1.get());
        }
    }


    public static Stream<Arguments> testOneMessageTwoTreadArguments() {
        return Stream.of(Arguments.of("Привет!", "Здорово!"),
                Arguments.of("Как дела?", "Отлично!"),
                Arguments.of("Чем занят?", "Отдыхаю!"),
                Arguments.of("Как настроение?", "Хорошее."),
                Arguments.of("Ты устал?", "Нет!"));
    }
}

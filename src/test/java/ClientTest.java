import client.Client;
import general.Read;
import general.ReadResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientTest {

    private static final File file = new File("config\\client\\settings.txt");
    private static final ReadResult result = new Read(file).read();
    private StringBuilder sb;
    private ExecutorService executorService;

    @BeforeEach
    public void beforeEach() {
        sb = new StringBuilder();
        executorService = Executors.newFixedThreadPool(1);
    }

    @AfterEach
    public void afterEach() {
        executorService.shutdown();
    }

    @ParameterizedTest
    @MethodSource("getArguments")
    public void sendLotsMessagesTest(Queue<String> messages, String name) throws IOException, ExecutionException, InterruptedException {
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(result.getIp(), result.getPort()));
            Future<SocketChannel> socketChannelFuture = executorService.submit(server::accept);
            Client client = new Client(messages::poll, sb::append, msg -> {});
            client.start(result.getIp(), result.getPort());
            SocketChannel socketChannel = socketChannelFuture.get();
            ByteBuffer byteBuffer = ByteBuffer.allocate(2 << 10);
            Future<List<String>> stringFuture = executorService.submit(() -> GeneralNIOActions.readListMessages(socketChannel, byteBuffer, messages.size() - 1));
            String excepted = new ArrayDeque<>(messages).stream()
                    .filter(x -> !x.equals("/exit"))
                    .map(msg -> name + ": " + msg)
                    .collect(Collectors.joining());
            String result = String.join("", stringFuture.get());
            Assertions.assertEquals(excepted, result);
        }
    }

    public static Stream<Arguments> getArguments() {
        return Stream.of(Arguments.of(new ArrayDeque<>(Arrays.asList("Привет", "Как дела?", "Как здоровье?", "/exit")), "Петя"),
                Arguments.of(new ArrayDeque<>(Arrays.asList("Здравствуйте!", "Удачного дня!", "/exit")), "Вася"),
                Arguments.of(new ArrayDeque<>(Arrays.asList("Всем привет!", "Как здоровье?", "Всем пока", "/exit")), "Александр"));
    }
}

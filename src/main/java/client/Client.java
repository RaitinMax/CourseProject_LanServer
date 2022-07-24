package client;

import general.Log;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Client {

    public static final String exitMessage = "/exit";
    private final Supplier<String> input;
    private final Consumer<String> output;
    private final Log log;

    public Client(Supplier<String> input, Consumer<String> output, Log log) {
        this.input = input;
        this.output = output;
        this.log = log;
    }

    public void start(String ip, int port) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress(ip, port));
            ByteBuffer byteBuffer = ByteBuffer.allocate(2 << 10);
            output.accept("Введите ваше имя!");
            String name = input.get();
            output.accept("Вы успешно подключились к чату!");
            Thread thread = new Thread(() -> read(byteBuffer, socketChannel));
            thread.start();
            write(socketChannel, name);
            thread.interrupt();
        } catch (Throwable throwable) {
            log.log(LocalDateTime.now() + " " + throwable);
        }
    }

    public void read(ByteBuffer byteBuffer, SocketChannel socketChannel) {
        Thread thread = Thread.currentThread();
        try {
            while (!thread.isInterrupted()) {
                int bytes = socketChannel.read(byteBuffer);
                String msg = new String(byteBuffer.array(), 0, bytes, StandardCharsets.UTF_8);
                byteBuffer.clear();
                log.log(LocalDateTime.now() + " " + msg);
                output.accept(msg);
            }
        } catch (ClosedByInterruptException e) {

        } catch (Throwable throwable) {
            log.log(LocalDateTime.now() + " " + throwable);
        }
    }

    public void write(SocketChannel socketChannel, String name) {
        try {
            while (true) {
                String msg = input.get();
                if (exitMessage.equals(msg)) {
                    log.log("Пользователь вышел из чата!");
                    break;
                }
                log.log(LocalDateTime.now() + " " + msg);
                String send = String.format("%s %s: %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), name, msg);
                socketChannel.write(ByteBuffer.wrap(send.getBytes(StandardCharsets.UTF_8)));
            }
        } catch (Throwable throwable) {
            log.log(LocalDateTime.now() + " " + throwable);
        }
    }
}

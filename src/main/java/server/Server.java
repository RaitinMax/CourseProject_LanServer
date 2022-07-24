package server;

import general.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final Log log;
    private volatile boolean isWork = false;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final List<SocketChannel> list = new CopyOnWriteArrayList<>();
    private final ServerSocketChannel serverSocketChannel;

    public Server(Log log) {
        this.log = log;
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
        } catch (IOException e) {
            log.log(LocalDateTime.now() + " " + e);
            throw new RuntimeException("Не удалось создать сервер!");
        }
    }

    public void start(String ip, int port) {
        if (isWork) {
            String msg = "Ошибка! Сервер уже запущен!";
            log.log(msg);
            throw new RuntimeException(msg);
        }
        try {
            serverSocketChannel.bind(new InetSocketAddress(ip, port));
            isWork = true;
            while (isWork) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                executorService.execute(() -> executorService.execute(() -> readMessage(socketChannel)));
            }
        } catch (Throwable throwable) {
            log.log(LocalDateTime.now() + " " + throwable);
        }
    }

    private void readMessage(SocketChannel socketChannel) {
        try (socketChannel) {
            list.add(socketChannel);
            ByteBuffer byteBuffer = ByteBuffer.allocate(2 << 10);
            while (socketChannel.isConnected()) {
                int bytesCount = socketChannel.read(byteBuffer);
                if (bytesCount == -1) {
                    log.log(LocalDateTime.now() +" Взаимодействие с клиентом прервано из-за невозможности работы буфера");
                    break;
                }
                String message = new String(byteBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                byteBuffer.clear();
                log.log(message);
                executorService.execute(() -> sendMessage(socketChannel, message));
            }
        } catch (SocketException e) {
            log.log(LocalDateTime.now() + " Пользователь вышел из чата!");
        } catch (Throwable throwable) {
            log.log(LocalDateTime.now() + " " + throwable);
        } finally {
            list.remove(socketChannel);
        }
    }

    private void sendMessage(SocketChannel socketChannel, String message) {
        try {
            list.stream()
                    .filter(x -> !x.equals(socketChannel))
                    .forEach(x -> {
                        try {
                            x.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
                        } catch (Throwable throwable) {
                            log.log(LocalDateTime.now() + " " + throwable);
                        }
                    });
        } catch (Throwable throwable) {
            log.log(LocalDateTime.now() + " " + throwable);
        }
    }

    public void stop() {
        try {
            isWork = false;
            executorService.shutdown();
            serverSocketChannel.close();
        } catch (Throwable throwable) {
            log.log(LocalDateTime.now() + " " + throwable);
        }
    }
}

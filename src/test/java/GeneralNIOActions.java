import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GeneralNIOActions {

    public static String readOneMessage(SocketChannel socketChannel, ByteBuffer byteBuffer) {
        try {
            int bytes = socketChannel.read(byteBuffer);
            String s = new String(byteBuffer.array(), 0, bytes, StandardCharsets.UTF_8);
            byteBuffer.clear();
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeOneMessage(String message, SocketChannel socketChannel) {
        try {
            socketChannel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readListMessages(SocketChannel socketChannel, ByteBuffer byteBuffer, int count) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                int bytes = socketChannel.read(byteBuffer);
                list.add(new String(byteBuffer.array(), 0, bytes, StandardCharsets.UTF_8));
                byteBuffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}

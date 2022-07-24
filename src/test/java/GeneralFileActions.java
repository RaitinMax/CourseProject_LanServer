import java.io.*;
import java.util.stream.Collectors;

public class GeneralFileActions {

    private GeneralFileActions() {}

    public static void clean(File file) {
        write(file, "");
    }

    public static String read(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            return bufferedReader.lines()
                    .collect(Collectors.joining());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void write(File file, String message) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

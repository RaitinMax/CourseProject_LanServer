package general;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger implements Log{

    private final File file;

    public Logger(File file) {
        this.file = file;
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String message) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))){
            bufferedWriter.write(message + "\n");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

package general;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Read {

    private final File settings;
    private final Log log;

    public Read(File settings, Log log) {
        this.settings = settings;
        this.log = log;
    }

    public Read(File settings) {
        this(settings, message -> {});
    }

    public ReadResult read() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(settings))) {
            String readString = bufferedReader.readLine();
            String[] info = readString.split(" ");
            return new ReadResult(info[0], Integer.parseInt(info[1]));
        } catch (Throwable throwable) {
            log.log(throwable.toString());
            return null;
        }
    }
}

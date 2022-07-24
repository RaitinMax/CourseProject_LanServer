package client;

import general.Log;
import general.Logger;
import general.Read;
import general.ReadResult;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {

    public static final File logFile = new File("logs\\client\\file.log");
    public static final Log log = new Logger(logFile);
    public static final File settings = new File("config\\client\\settings.txt");
    public static final Read read = new Read(settings, log);
    public static final ReadResult result = read.read();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Client client = new Client(scanner::nextLine, System.out::println, log);
            client.start(result.getIp(), result.getPort());
        } catch (Throwable throwable) {
            log.log(LocalDateTime.now() + " " + throwable);
        }
    }
}

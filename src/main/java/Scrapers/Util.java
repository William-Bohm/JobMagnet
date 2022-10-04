package scrape;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {
    public static void test() {
        System.out.println("heyy this might actully work");
    }

    public static String createCsvFile(String fileName, String directoryName) throws IOException {
        // get date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMMyyyy");
        LocalDateTime now = LocalDateTime.now();

        // format file name
        String csvName = fileName.split("\\s")[0] + dtf.format(now) + ".csv";
        csvName = csvName.replaceAll("\\s", "");

        // create file
        System.out.println(csvName);
        Path path = Paths.get(directoryName + csvName);
        String pathStr = directoryName + csvName;
        System.out.println(directoryName + csvName);
        if (!Files.exists(path)) {
            try {
                // create csv file
                Files.createFile(path);
                System.out.println("CSV file created, named: " + csvName);
            } catch  (IOException e){
                System.out.println("Failed to create CSV file!" + e.getMessage());
            }
        } else {
            System.out.println("CSV file already exist! :)");
        }
        return pathStr;
    }

    public static void createDataDirectory(String directoryName) throws IOException {
        // check if data storage directory exist, if not create one
        Path path = Paths.get(directoryName);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
                System.out.println("Data Directory Created!");
            } catch (IOException e){
                System.err.println("Failed to create directory!" + e.getMessage());
            }
        } else {
            System.out.println("Data Directory already exist! :)");
        }
    }



}

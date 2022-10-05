package Scrapers;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Hashtable;

public class Util {
    public static void test() {
        System.out.println("heyy this might actully work");
    }

    public static String createCsvFile(String fileName, String directoryName, String date) throws IOException {
        // format file name
        String csvName = fileName.split("\\s")[0] + date + ".csv";
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

    public static String[] createAllDirectories(String[] jobSites, String Job, String Location, String Date) throws IOException {
        // get current directory of project
        String baseDirectory = System.getProperty("user.dir");
        String[] directories = new String[jobSites.length];

        // format strings
        Job = Job.replaceAll("\\s", "_");
        Location = Location.replaceAll("\\s", "");

        // create directories for each job
        for (int i = 0; i < jobSites.length; i++) {
            try {
                String directoryName = baseDirectory + "\\" + "Data" + "\\" +jobSites[i] + "\\";
                createSingleDirectory(directoryName);
                createSingleDirectory(directoryName + Date + "\\");
                createSingleDirectory(directoryName + Date + "\\" + Location + "\\");
                createSingleDirectory(directoryName + Date + "\\" + Location + "\\" + Job + "\\");
                directories[i] = directoryName;
            } catch (Exception e) {
                System.out.println("Unable to create data Directories" + e.getMessage());
            }
        }
        return directories;
    }

    private static void createSingleDirectory(String directoryName) throws IOException {
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

    public static WebDriver createChromeDriver() {
        WebDriver driver;
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        return driver;
    }

    public static String getDate() {
        // get date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMMyyyy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}

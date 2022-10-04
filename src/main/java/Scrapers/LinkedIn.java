package Scrapers;

import com.opencsv.CSVWriter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LinkedIn {

    public static WebDriver driver;


    public static void main(String[] args) throws IOException, InterruptedException {
        // login credentials
        String username = "wsbohm@gmail.com";
        String password = "Bababooey98%$";


        // define constants
        String jobTitle = "software engineer I";
        String jobZipCode = "27518";

        String dataDirectoryPath = "C:\\Users\\wsboh\\documents\\Programming\\java\\web_scrape\\JobMagnet\\data\\LinkedIn\\";
        String csvFileName = jobTitle + "," + "Linkedin";

        // create files and drivers
        scrape.Util.createDataDirectory(dataDirectoryPath);
        String csvpath = scrape.Util.createCsvFile(csvFileName, dataDirectoryPath);
        createChromeDriver();

        // setup csv writer
        FileWriter outputfile = new FileWriter(csvpath);
        CSVWriter writer = new CSVWriter(outputfile);
        String[] header = { "jobTile", "companyName", "city", "estimated", "salary", "postDate", "jobType"};
        writer.writeNext(header);


        // create
        loginLinkedin(username, password);
        searchJob(jobTitle, jobZipCode);

        int pageNumber = 1;
        while (true) {
            //getJobData();
            pageNumber = nextPage(pageNumber);
            Thread.sleep(10000);
            if (pageNumber == 9999) {
                break;
            }
        }

    }
    public static void createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
    }


    public static void loginLinkedin(String username, String password) throws InterruptedException {
        driver.get("https://www.linkedin.com/");
        WebElement signInButton = driver.findElement(new By.ByLinkText("Sign in"));
        signInButton.click();

        WebElement emailField = driver.findElement(By.id("username"));
        emailField.sendKeys(username);

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys(password);

        Thread.sleep(2000);

        WebElement loginSubmitButton = driver.findElement(By.xpath("//button[@aria-label=\"Sign in\"]"));
        loginSubmitButton.click();
    }

    public static void searchJob (String job, String location) throws InterruptedException {
        // go to job search page
        WebElement searchField = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-label=\"Search\"]")));
        searchField.sendKeys(job);
        searchField.sendKeys(Keys.ENTER);

        // click see all jobs button to go to main job search page
        WebElement seeJobsButton = new WebDriverWait(driver, Duration.ofSeconds(4))
                .until(ExpectedConditions.elementToBeClickable(By.partialLinkText("See all job results in")));
        seeJobsButton.click();
        System.out.println("job");

        // enter in location
        WebElement locationField = new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-label=\"City, state, or zip code\"]")));
        locationField.click();
        Thread.sleep(2000);
        locationField.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        locationField.sendKeys(location);
        System.out.println("location");

        Thread.sleep(2000);

        locationField.sendKeys(Keys.ENTER);
    }

    private static int nextPage (int currentPageNum) throws InterruptedException {
        Thread.sleep(2000);
        List<WebElement> pageList= driver.findElements(By.xpath("//ul[@class=\"artdeco-pagination__pages artdeco-pagination__pages--number\"]/li"));

        for (WebElement page: pageList) {
            String pageNumStr = page.getText();
            try {
                int pageNum = Integer.parseInt(pageNumStr);
                if (pageNum == currentPageNum + 1) {
                    page.findElement(By.tagName("button")).click();
                    currentPageNum++;
                    break;
                }
            } catch (Exception e){
                try {
                    page.findElement(By.tagName("button")).click();
                    currentPageNum++;
                    break;
                } catch (Exception ex){
                    System.out.println("couldnt find correct page number to go to :( \n"+ ex.getMessage());
                    return 99999;
                }
            }
        }
        return currentPageNum;
    }

    private static void getJobData () throws InterruptedException {
        // ************* the list elements are stale which means i cannot loop through them after they've been found :(
        // get data frome each job
        List<WebElement> jobs = driver.findElements(By.xpath("//ul[@class=\"scaffold-layout__list-container\"]/li"));
        int jobNum = jobs.size();
        for (int i = 0; i < jobNum - 2;  i++) {
            List<WebElement> jobPages = driver.findElements(By.xpath("//ul[@class=\"scaffold-layout__list-container\"]/li"));
            WebElement job = jobPages.get(i);
            job.click();
            Thread.sleep(3000);

        }
    }

    private static void parseJobData(WebElement jobPage) {
        String companyName = jobPage.findElement(By.xpath("//span[@class=\"jobs-unified-top-card__company-name\"]")).getText();
        String jobTitle = jobPage.findElement(By.xpath("//h2[@class=\"t-24 t-bold jobs-unified-top-card__job-title\"]")).getText();
        System.out.println(companyName + jobTitle);
        // get info via, readin all text and parsing it
        List<WebElement> job_insight = jobPage.findElements(By.className("jobs-unified-top-card__job-insight"));
        for (WebElement insight: job_insight) {
            String info = insight.getText();
            System.out.println(info);
        }
        System.out.println("-----------------");
    }
}

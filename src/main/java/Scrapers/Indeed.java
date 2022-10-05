package Scrapers;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.CSVWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Indeed {
    static WebDriver driver;
    String webAddress;
    static String dataDirectoryPath = "C:\\Users\\wsboh\\documents\\Programming\\java\\web_scrape\\JobMagnet\\data\\Indeed\\";


    public static void main(String jobTitle, String jobLocation, WebDriver chromeDriver) throws InterruptedException, IOException {
        String csvFileName = jobTitle + "," + jobLocation;

        driver = chromeDriver;


        // create files and drivers
        String csvpath = Util.createCsvFile(csvFileName, dataDirectoryPath, "12072223");
        driver = Util.createChromeDriver();

        // setup csv writer
        FileWriter outputfile = new FileWriter(csvpath);
        CSVWriter writer = new CSVWriter(outputfile);
        String[] header = { "jobTile", "companyName", "city", "estimated", "salary", "postDate", "jobType"};
        writer.writeNext(header);

        //  load indeed and set designated parameters
        driver.get("https://www.indeed.com/");
        searchFields(jobTitle, jobLocation);
        setIndeedParams(null, null, false);

        // main loop
        int pagenum = 0;
        while (true) {
            pagenum++;
            // get job info from the webpage
            String[][] jobInfo = getPageInfo();
            // write job info to csv
            for (String[] job: jobInfo) {
                writer.writeNext(job);
            }
            System.out.println("finished page " + pagenum);
            Thread.sleep(7000);

            if (!clickNextPage()) {
                break;
            }
        }
        writer.close();
    }

    public static void searchFields(String job, String location) {
        // search for the job and location
        //        search for job and location
        WebElement jobSearchBar = driver.findElement(By.name("q"));
        jobSearchBar.sendKeys(job);

        WebElement locationSearchBar = driver.findElement(By.name("l"));
        locationSearchBar.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        locationSearchBar.sendKeys(location);

        WebElement searchButton = driver.findElement(By.className("yosegi-InlineWhatWhere-primaryButton"));
        searchButton.click();
    }

    public static void setIndeedParams(String postDate , String miles, boolean remote) {
        // select preset paramaters from the indeed options
        if(miles != null) {
            String distance = "within " + miles + " miles";
            WebElement distanceButtton = driver.findElement(By.xpath("//button[@id='filter-radius']"));
            distanceButtton.click();
            List<WebElement> distanceOptions = distanceButtton.findElements(By.xpath("//ul[@id='filter-radius-menu']/li"));
            for(WebElement e: distanceOptions) {
                String text = e.getText();
                if(text.equals(distance)) {
                    e.click();
                    break;
                }
            }
            cancelEmailPopUp();
        }
        if(postDate != null) {
            String datesuffix = " days";
            if(postDate.equals("24")) {datesuffix = " hours";}
            String date = "Last " + postDate + datesuffix;
            WebElement dateButton = driver.findElement(By.xpath("//button[@id='filter-dateposted']"));
            dateButton.click();
            List<WebElement> dateoptions = dateButton.findElements(By.xpath("//ul[@id='filter-dateposted-menu']/li"));
            for(WebElement e: dateoptions) {
                String text = e.getText();
                if(text.equals(date)) {
                    e.click();
                    break;
                }
            }
            cancelEmailPopUp();
        }
        if(remote) {
            WebElement remoteButton = driver.findElement(By.xpath("//button[@id='filter-remotejob']"));
            remoteButton.click();
            List<WebElement> remoteoptions = remoteButton.findElements(By.xpath("//ul[@id='filter-remotejob-menu']/li"));
            Pattern remoteRegex = Pattern.compile("^Remote");
            for(WebElement e: remoteoptions) {
                String text = e.getText();
                Matcher matcher = remoteRegex.matcher(text);
                boolean matchFound = matcher.find();
                if(matchFound) {
                    e.click();
                    break;
                }
            }
            cancelEmailPopUp();
        }
    }

    public static String[][] getPageInfo() throws InterruptedException {
        // retrieve and format the data
        String[][] jobArray = new String[15][];
        int count = 0;
//        parse the jobs list
        List<WebElement> jobs3 = driver.
                findElements(By.xpath("//ul[@class='jobsearch-ResultsList css-0']/li/div[@class!='mosaic-zone']"));

        for (WebElement e: jobs3) {
            // get job post text
            String info = e.getText();
            // format job post text
            String[] infoArray = info.split("\n");
            int arrayLength = infoArray.length;
            String[] finalInfoArray = new String[7];
            Arrays.fill(finalInfoArray, "NA");
            int index = 2;
            // job title
            finalInfoArray[0] = infoArray[0];
            // company name
            if (infoArray[1].equals("new")) {finalInfoArray[1] = infoArray[2]; index = 3;}
            else {finalInfoArray[1] = infoArray[1];}
            // city
            finalInfoArray[2] = infoArray[index].replaceAll("Hybrid|Remote|in|[0-9]", ""); index++;
            // estimated or stated salary
            if (infoArray[index].contains("Estimated")) {
                finalInfoArray[3] = "Estimated";
            } else {finalInfoArray[3] = "Stated";}



            for (int i = index; i <= arrayLength - 1; i++) {
                //  salary
                if (infoArray[i].contains("$")){
                    String salary = infoArray[i].replaceAll("Estimated|an|hour|year|,|a|K|[$]|\\s", "");
                    finalInfoArray[4] = salary;
                    continue;
                }
                // date posted
                if (infoArray[i].startsWith("Today")) {finalInfoArray[5] = "0"; continue;}
                if (infoArray[i].contains("Just posted")) {finalInfoArray[5] = "0"; continue;}
                if (infoArray[i].contains("days")) {
                    finalInfoArray[5] = infoArray[i].replaceAll("[^0-9]", "");
                    continue;
                }
                if (infoArray[i].contains("day")) {finalInfoArray[5] = "1"; continue;}
                // full-time part-time contract
                if (infoArray[i].startsWith("Full-time")){finalInfoArray[6] = "full-time"; continue;}
                if (infoArray[i].startsWith("Part-time")){finalInfoArray[6] = "part-time"; continue;}
                if (infoArray[i].startsWith("Contract")){finalInfoArray[6] = "Contract";}
            }
            // [job title, company name, city, estimated salary?, salary, date posted, full-part-contract]
            jobArray[count] = finalInfoArray;
            count++;
        }
        return jobArray;
    }

    public static boolean clickNextPage() {
        try {
            try {
                WebElement next = driver.findElement(By.xpath("//a[@aria-label='Next']"));
                next.click();
            } catch (Exception e) {
                WebElement next = driver.findElement(By.xpath("//a[@aria-label='Next Page']"));
                next.click();
            }
            cancelEmailPopUp();
            return true;
        } catch (Exception e) {
            System.out.println("no more pages could be found :(");
            return false;
        }
    }

    public static void cancelEmailPopUp() {
        try {
            WebElement x = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='popover-x']/button")));
            x.click();
        }
        catch (Exception e) {
            System.out.println("no popup occoured");
        }
    }

}
























import Scrapers.Indeed;
import Scrapers.Util;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // parameters
        String[] jobSites = new String[] {"Indeed"};
        String jobTitle = "python developer";
        String Location = "Raleigh, NC";

        // automatic parameters
        WebDriver driver = Util.createChromeDriver();
        String date = Util.getDate();

        // create data directories
        // String[] dataDirectories = Util.createAllDirectories(jobSites, jobTitle, Location, date);
        Hashtable windowHandles = getWebsites(jobSites, driver);
        System.out.println(windowHandles.toString());


        // MAIN LOOP!!!!!
//        while (true) {
//            Thread.sleep(7000);
//            System.out.println("stuck in loop :O");
//        }


        //Indeed.main("Python", "Raleigh, NC", driver);
        Thread.sleep(7000);
        driver.quit();
    }

    public static Hashtable getWebsites(String[] jobSites, WebDriver driver) {
        // creates a tab for each website and returns a hastable of the tab handles
        // to allow switching of tabs.

        Hashtable<String, String> URLs = new Hashtable<>();
        URLs.put("Indeed", "https://www.indeed.com/");
        URLs.put("LinkedIn", "https://www.linkedin.com/");
        URLs.put("GlassDoor", "https://www.glassdoor.com/index.htm");
        URLs.put("ZipRecruiter", "https://www.ziprecruiter.com/");
        URLs.put("GoogleJobs", "https://www.google.com/");

        String[] tempHandles = new String[jobSites.length];
        Hashtable<String, String> windowHandles = new Hashtable<>();

        driver.get(URLs.get(jobSites[0]));
        windowHandles.put(jobSites[0], driver.getWindowHandle());
        tempHandles[0] = driver.getWindowHandle();
        assert driver.getWindowHandles().size() == 1;
        for (int i = 1; i < jobSites.length; i++) {
            driver.switchTo().newWindow(WindowType.TAB);
            driver.get(URLs.get(jobSites[i]));
            String handle = driver.getWindowHandle();
            if (!Arrays.asList(tempHandles).contains(handle)) {
                tempHandles[i] = handle;
                windowHandles.put(jobSites[i], handle);
            }
        }
        return windowHandles;
    }


}

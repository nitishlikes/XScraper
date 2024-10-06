package demo;

import java.util.*;
import java.io.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.util.logging.Level;
import demo.wrappers.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestCases {
    ChromeDriver driver;

    @Test
    public void testCase01() throws InterruptedException {
        System.out.println("Stating Test Case 01");

        driver.get("https://www.scrapethissite.com/pages");
        WebElement teams = driver.findElement(By.partialLinkText("Hockey Teams"));
        Wrappers.clickOnElement(driver, teams);

        // Initialize and declare a HashMap ArrayList
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();

        // clicking on page 1
        WebElement page_1 = driver.findElement(By.xpath("(//ul[@class='pagination']/li/a)[1]"));
        Wrappers.clickOnElement(driver, page_1);

        // Iterate through pages
        for (int i = 1; i <= 4; i++) {
            List<WebElement> rows = driver.findElements(By.xpath("//tr[@class='team']"));

            // Extract data from each row
            for (WebElement row : rows) {
                String teamName = row.findElement(By.xpath(".//td[@class='name']")).getText();
                int year = Integer.parseInt(row.findElement(By.xpath(".//td[@class='year']")).getText());
                Double win = Double.parseDouble(row.findElement(By.xpath(".//td[contains(@class,'pct')]")).getText());

            //Get EpochTime
                long epoch = System.currentTimeMillis()/1000;  
                
            // Check if win percentage is less than 40
                if (win < 0.4) {

                    //Create a HashMap to store data
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Epoch Time of Scrape", epoch);
                    map.put("Team Name", teamName);
                    map.put("Year", year);
                    map.put("Win %", win);

            // Add the HashMap to the ArrayList
                    data.add(map);

                }    
            }
            // navigate to next page
            if (i < 4) {
                WebElement next_page = driver.findElement(By.xpath("//a[@aria-label='Next']"));
                next_page.click();
                Thread.sleep(5000);
            }
            // Store the HashMap Data in a json File
            ObjectMapper mapper = new ObjectMapper();
            try {
                String userDir = System.getProperty("user.dir");
                File file = new File(userDir + "/src/test/resources/hockey-team-data.json.json");
                mapper.writeValue(file, data);
                Assert.assertTrue(file.length() != 0);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("End Test Case 01");
    }

    @Test
    public void testCase02() {
        System.out.println("Stating Test Case 02");

        driver.get("https://www.scrapethissite.com/pages");
        WebElement films = driver.findElement(By.partialLinkText("Oscar Winning Films"));
        Wrappers.clickOnElement(driver, films);

        Wrappers.scrape(driver, "2015");
        Wrappers.scrape(driver, "2014");
        Wrappers.scrape(driver, "2013");
        Wrappers.scrape(driver, "2012");
        Wrappers.scrape(driver, "2011");
        Wrappers.scrape(driver, "2010");

        System.out.println("End Test Case 02");
    }


    @BeforeTest
    public void startBrowser()
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }

    @AfterTest
    public void endTest()
    {
        driver.close();
        driver.quit();

    }
}
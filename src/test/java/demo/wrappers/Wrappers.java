package demo.wrappers;

import java.io.*;
import java.util.*;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Wrappers {
    
    public static boolean clickOnElement(WebDriver driver, WebElement element) {
        if (element.isDisplayed()) {
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView();", element);
                element.click();
                Thread.sleep(3000);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
            return false;
    }

    public static boolean enterText(WebDriver driver, WebElement element, String s) {
        try {
            element.click();
            element.clear();
            element.sendKeys(s);
            Thread.sleep(2000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void scrape(WebDriver driver, String year) {
        
            WebElement year_to_click = driver.findElement(By.id(year));
            String year_text = year_to_click.getText();
            clickOnElement(driver, year_to_click);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='table']")));

        // Initialize and declare a HashMap ArrayList
            ArrayList<HashMap<String, String>> data = new ArrayList<>();

        //Collecting all required data    
            int count = 1;
            for (int i = 0; i < 5; i++) {
            List<WebElement> rows = driver.findElements(By.xpath("//tr[@class='film']"));
                for(WebElement row : rows) {
                    String title = row.findElement(By.xpath(".//td[@class='film-title']")).getText();
                    String nominations = row.findElement(By.xpath(".//td[@class='film-nominations']")).getText();
                    String awards = row.findElement(By.xpath(".//td[@class='film-awards']")).getText();
                    boolean flag = count == 1;
                    String isWinner = String.valueOf(flag);
                    long epoch = System.currentTimeMillis()/1000;
                    String epochTime = String.valueOf(epoch);

                    HashMap<String, String> map = new HashMap<>();
                    map.put("Epoch Time of Scrape", epochTime);
                    map.put("Year", year_text);
                    map.put("Title", title);
                    map.put("Nomination", nominations);
                    map.put("Awards", awards);
                    map.put("isWinner", isWinner);

        // Add the HashMap to the ArrayList        
                    data.add(map);
                    count++;
                }

        // Store the HashMap Data in a json File
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String userDir = System.getProperty("user.dir");
                    File file = new File(userDir + "/src/test/resources/oscar-winner-data.json");
                    mapper.writeValue(file, data);
                    Assert.assertTrue(file.length() != 0);
            }   catch(IOException e) {
                    e.printStackTrace();
                    }        
                }
            }
        }
    


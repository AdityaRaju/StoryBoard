package com.test1;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/23/13
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeleniumTest1 {

    static WebDriver driver;
    static Wait<WebDriver> wait;

    public static void main(String[] args) {
        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, 30);
        driver.get("http://www.google.com/");
        //driver.

        boolean result;
        try {
            result = firstPageContainsQAANet();
        } catch(Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            driver.close();
        }

        System.out.println("Test " + (result? "passed." : "failed."));
        if (!result) {
            System.exit(1);
        }
    }

    private static boolean firstPageContainsQAANet() {
        //type search query
        driver.findElement(By.name("q")).sendKeys("qa automation\n");

        // click search
        driver.findElement(By.name("btnG")).click();

        // Wait for search to complete
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver) {
                System.out.println("Searching ...");
                return webDriver.findElement(By.id("resultStats")) != null;
            }
        });

        // Look for QAAutomation.net in the results
        return driver.findElement(By.tagName("body")).getText().contains("qaautomation.net");
    }
}

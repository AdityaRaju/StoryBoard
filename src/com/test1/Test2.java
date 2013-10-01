package com.test1;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;


/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/24/13
 * Time: 10:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test2 {
    public static void main(String[] args)throws Exception {
        Selenium selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://www.google.com");
        DesiredCapabilities capability = DesiredCapabilities.firefox();
        System.out.println("selenium = " + selenium.getHtmlSource());
        WebDriver driver = new FirefoxDriver();
        //driver.manage().window().

        // And now use this to visit Google
        driver.get("http://www.google.com");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());

        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });

        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
// Now you can do whatever you need to do with it, for example copy somewhere
        FileUtils.copyFile(scrFile, new File("/Users/dosapati/temp/screenshot.png"));

        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());

        //Close the browser
        driver.quit();
    }
}

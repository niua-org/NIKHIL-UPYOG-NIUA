package org.upyog.Automation.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class WebDriverFactory {

    @Value("${selenium.grid.url:http://selenium-chrome:4444}")
    private String gridUrl;

    @Value("${selenium.grid.enabled:false}")
    private boolean gridEnabled;

    // Track active WebDriver instances
    private static final List<WebDriver> activeDrivers = new ArrayList<>();

    public WebDriver createDriver() {
        try {
            // Close any existing drivers before creating a new one
            closeAllDrivers();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--start-maximized");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            WebDriver driver;
            if (gridEnabled) {
                driver = new RemoteWebDriver(new URL(gridUrl), options);
            } else {
                // Automatically download and setup the correct ChromeDriver version
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(options);
            }

            // Track this driver
            activeDrivers.add(driver);
            return driver;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create WebDriver: " + e.getMessage(), e);
        }
    }

    /**
     * Close all active WebDriver instances
     */
    public static void closeAllDrivers() {
        for (WebDriver driver : activeDrivers) {
            try {
                if (driver != null) {
                    driver.quit();
                }
            } catch (Exception e) {
                // Ignore errors when closing
            }
        }
        activeDrivers.clear();
    }
}

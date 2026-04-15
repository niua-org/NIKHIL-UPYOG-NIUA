package org.upyog.Automation.Modules.OBPAS;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.upyog.Automation.Utils.ConfigReader;
import org.upyog.Automation.Utils.DriverFactory;

@Component


public class OBPASOcCreate {

    //@PostConstruct

    public void OBPASOCReg() {
        OBPASOCReg(ConfigReader.get("citizen.base.url"),
                "OBPAS",
                ConfigReader.get("architect.mobile.number"),
                ConfigReader.get("test.otp"),
                ConfigReader.get("test.city.name"),
                ConfigReader.get("permit.number"));
    }

    public void OBPASOCReg(String baseUrl, String moduleName, String mobileNumber, String otp, String cityName, String permitNumber) {
        System.out.println("OBPAS Registration by Citizen");

        WebDriver driver = DriverFactory.createChromeDriver();
        WebDriverWait wait = DriverFactory.createWebDriverWait(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);

        try {
            // STEP 1: Architect Login
            performArchitectLogin(driver, wait, js, actions, baseUrl, mobileNumber, otp, cityName);

            // STEP 2: Navigate to OBPAS Module
            navigateToOBPAS(driver, wait, js);

            // STEP 3: Building Approval Plan
            NewBuildingPlanScrutiny(driver, wait, js);

        } catch (Exception e) {
            System.out.println("Exception in OBPAS Registration: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // driver.quit();
        }
    }

             /*
             =====================================================================
             STEP 1: ARCHITECT LOGIN
             =====================================================================
             */

    private void performArchitectLogin(WebDriver driver, WebDriverWait wait, JavascriptExecutor js, Actions actions, String baseUrl, String mobileNumber, String otp, String cityName)
            throws InterruptedException {

        driver.get(baseUrl);
        System.out.println("Open the Citizen Login Portal");

        // Mobile number
        fillInput(wait, "mobileNumber", mobileNumber);

        // Accept terms checkbox
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[type='checkbox'].form-field")));
        if (!checkbox.isSelected()) {
            js.executeScript("arguments[0].click();", checkbox);
            Thread.sleep(1000);
        }

        // Next
        clickButton(wait, js, "//button[@type='submit']//header[text()='Next']/..");

        // OTP
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.input-otp-wrap")));
        List<WebElement> otpInputs = driver.findElements(By.cssSelector("input.input-otp"));
        for (int i = 0; i < otp.length() && i < otpInputs.size(); i++) {
            otpInputs.get(i).sendKeys(String.valueOf(otp.charAt(i)));
        }

        // Submit OTP
        clickButton(wait, js, "//button[@type='submit']//header[text()='Next']/..");

        // Select city`1
        selectCity(driver, wait, js, cityName);

        // Continue
        WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'submit-bar') and contains(., 'Continue')]")));
        js.executeScript("arguments[0].scrollIntoView(true);", continueBtn);
        actions.moveToElement(continueBtn).click().perform();
    }

             /*
             =====================================================================
             STEP 2: NAVIGATE TO OBPAS MODULE
             =====================================================================
             */

    private void navigateToOBPAS(WebDriver driver, WebDriverWait wait, JavascriptExecutor js)
            throws InterruptedException {

        System.out.println("Navigating to OBPAS");

        // Sidebar Property Tax link
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[@href='/upyog-ui/citizen/obps-home']"))));

        Thread.sleep(2000);
        System.out.println("Reached OBPAS home page");

        // "Registered Architect Login" link
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='/upyog-ui/citizen/obps/home']"))));

        System.out.println("Clicked Registered Architect Login link");

        // "Plan scrutiny for new construction" link
        WebElement newConstruction = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'OC Plan Scrutiny for new Construction')]")
                )
        );

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", newConstruction);
        Thread.sleep(500);

        js.executeScript("arguments[0].click();", newConstruction);

        System.out.println("Clicked Plan scrutiny for new construction");
        Thread.sleep(2000);

    }

            /*
             =====================================================================
             STEP 3: NEW BUILDING PLAN SCRUTINY
             =====================================================================
            */

    private void NewBuildingPlanScrutiny(WebDriver driver, WebDriverWait wait, JavascriptExecutor js)
            throws InterruptedException {

        System.out.println("OBPAS Info Page - Clicking Next");
        Thread.sleep(2000);

        // Select city dropdown
        try {
            selectDropdownByIndex(driver, wait, js, 0, 0);
            Thread.sleep(1000);
            System.out.println("Selected city");
        } catch (Exception e) {
            System.out.println("City dropdown not found: " + e.getMessage());
        }

        Thread.sleep(1000);

        System.out.println("Filling Applicant Details");
        fillInput(wait, "applicantName", "Arpit Rao");
        Thread.sleep(500);

        System.out.println("Uploading the DXF file");
        uploadDxf(driver, wait, js, 0, ConfigReader.get("document.drawing.dxf"));
        Thread.sleep(3000);
        System.out.println("Finished Upload Documents step");

        WebElement submitBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(@class,'submit-bar') and @type='submit']")
                )
        );

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);
        Thread.sleep(300);

        submitBtn.click();
        Thread.sleep(10000);

        System.out.println("Clicked SUBMIT button");
    }

     /*
             =====================================================================
             UTILITY METHODS
             =====================================================================
            */

    private void fillInput(WebDriverWait wait, String fieldName, String value) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.name(fieldName)));
        input.clear();
        input.sendKeys(value);
    }

    // optional field – do not fail if missing
    private void fillOptionalInput(WebDriver driver, WebDriverWait wait, String fieldName, String value) {
        try {
            WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(By.name(fieldName)));
            if (input.isDisplayed() && input.isEnabled()) {
                input.clear();
                input.sendKeys(value);
                System.out.println("Filled optional field: " + fieldName);
            } else {
                System.out.println("Optional field " + fieldName + " not interactable, skipping");
            }
        } catch (Exception e) {
            System.out.println("Optional field " + fieldName + " not found, skipping");
        }
    }

    private void fillInputField(JavascriptExecutor js, WebElement input, String value)
            throws InterruptedException {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", input);
        input.click();
        input.clear();
        input.sendKeys(value);
        js.executeScript("arguments[0].dispatchEvent(new Event('change',{bubbles:true}));", input);
        Thread.sleep(200);
    }

    private void clickButton(WebDriverWait wait, JavascriptExecutor js, String xpath) throws InterruptedException {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        js.executeScript("arguments[0].scrollIntoView(true);", button);
        Thread.sleep(300);
        button.click();
    }

    private void clickButtonByHeader(WebDriver driver, WebDriverWait wait, String headerText)
            throws InterruptedException {

        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'submit-bar') and .//header[contains(text(),'" + headerText + "')]]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        Thread.sleep(500);
    }

    private void clickNextButton(WebDriver driver, WebDriverWait wait, JavascriptExecutor js)
            throws InterruptedException {

        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class,'submit-bar') and .//header[text()='Next']]")));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", nextButton);
        Thread.sleep(200);
        nextButton.click();
        System.out.println("Clicked Next");
    }

    private void selectCity(WebDriver driver, WebDriverWait wait, JavascriptExecutor js, String cityName)
            throws InterruptedException {

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.radio-wrap.reverse-radio-selection-wrapper")));

        List<WebElement> cityOptions = driver.findElements(
                By.cssSelector("div.radio-wrap.reverse-radio-selection-wrapper div"));

        for (WebElement option : cityOptions) {
            WebElement label = option.findElement(By.tagName("label"));
            if (label.getText().trim().equals(cityName)) {
                WebElement radioInput = option.findElement(By.cssSelector("input[type='radio']"));
                if (!radioInput.isSelected()) {
                    js.executeScript("arguments[0].click();", radioInput);
                    Thread.sleep(1000);
                }
                return;
            }
        }
        throw new RuntimeException("Failed to select city: " + cityName);
    }

    private void selectRadioButtonByLabel(WebDriver driver, String labelText) {
        try {
            WebElement radio = null;

            try {
                radio = driver.findElement(By.xpath("//label[text()='" + labelText + "']/preceding-sibling::span/input"));
            } catch (Exception e1) {
                try {
                    radio = driver.findElement(By.xpath("//label[contains(text(),'" + labelText + "')]/preceding-sibling::input"));
                } catch (Exception e2) {
                    try {
                        radio = driver.findElement(By.xpath("//label[text()='" + labelText + "']/..//input[@type='radio']"));
                    } catch (Exception e3) {
                        try {
                            radio = driver.findElement(By.xpath("//label[text()='" + labelText + "']/following-sibling::input[@type='radio']"));
                        } catch (Exception e4) {
                            radio = driver.findElement(By.xpath("//input[@type='radio'][@value='" + labelText + "']"));
                        }
                    }
                }
            }

            if (radio != null && !radio.isSelected()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", radio);
                Thread.sleep(200);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radio);
                System.out.println("Selected radio button: " + labelText);
            }
        } catch (Exception e) {
            System.out.println("Error selecting radio button '" + labelText + "': " + e.getMessage());
            throw new RuntimeException("Failed to select radio button: " + labelText, e);
        }
    }

    private void selectDropdownOption(WebDriver driver,
                                      WebDriverWait wait,
                                      JavascriptExecutor js,
                                      int dropdownIndex) throws InterruptedException {

        // get all dropdown arrow svgs on the page
        java.util.List<WebElement> dropdownSvgs = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector("div.select svg.cp"))
        );

        if (dropdownIndex < 0 || dropdownIndex >= dropdownSvgs.size()) {
            System.out.println("Dropdown index " + dropdownIndex + " not found. Total: " + dropdownSvgs.size());
            return;
        }

        WebElement svg = dropdownSvgs.get(dropdownIndex);

        // scroll into view
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", svg);
        Thread.sleep(200);
    }

    private void selectDropdownByIndex(WebDriver driver, WebDriverWait wait, JavascriptExecutor js,
                                       int dropdownIndex,
                                       int optionIndex)
            throws InterruptedException {

        List<WebElement> dropdowns = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector("div.select svg.cp")
                )
        );

        WebElement dropdown = dropdowns.get(dropdownIndex);

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", dropdown);
        Thread.sleep(200);

        try {
            dropdown.click();
            Thread.sleep(1000);
        } catch (Exception e) {
            js.executeScript(
                    "var ev = document.createEvent('MouseEvents');" +
                            "ev.initEvent('click', true, true);" +
                            "arguments[0].dispatchEvent(ev);",
                    dropdown
            );
        }

        WebElement optionsContainer = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.options-card")
                )
        );

        List<WebElement> options = optionsContainer.findElements(
                By.cssSelector("div.profile-dropdown--item")
        );

        WebElement option = options.get(optionIndex);

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", option);
        js.executeScript("arguments[0].click();", option);

        Thread.sleep(300);
    }


    private void uploadFile(WebDriver driver, WebDriverWait wait, JavascriptExecutor js,
                            int index, String filePath) throws InterruptedException {

        List<WebElement> fileInputs = driver.findElements(By.cssSelector("input[type='file']"));

        if (index >= fileInputs.size()) {
            System.out.println("ERROR: File input index " + index + " not found");
            return;
        }

        WebElement fileInput = fileInputs.get(index);

        js.executeScript(
                "arguments[0].style.cssText = 'display:block !important; visibility:visible !important; opacity:1 !important;';",
                fileInput
        );
        Thread.sleep(300);

        fileInput.sendKeys(filePath);
        System.out.println("✓ Uploaded file at index " + index);

        js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", fileInput);
        Thread.sleep(500);
    }



    private void uploadDocumentByLabel(WebDriver driver,
                                       JavascriptExecutor js,
                                       String labelText,
                                       String filePath) throws InterruptedException {

        System.out.println("Uploading for: " + labelText);

        // Find document card by label
        WebElement docSection = driver.findElement(By.xpath(
                "//div[contains(@class,'upload-file')]//preceding::h2[contains(.,'"
                        + labelText + "')][1]/ancestor::div[contains(@class,'card')]"
        ));

        WebElement fileInput = docSection.findElement(By.xpath(".//input[@type='file']"));

        js.executeScript(
                "arguments[0].style.cssText = 'display:block !important; visibility:visible !important; opacity:1 !important; position:relative !important;';",
                fileInput
        );

        Thread.sleep(500);

        fileInput.sendKeys(filePath);

        js.executeScript(
                "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
                fileInput
        );

        Thread.sleep(1500);

        System.out.println("Uploaded file for " + labelText);
    }



    private void selectRadioByLabel(WebDriver driver,
                                    WebDriverWait wait,
                                    JavascriptExecutor js,
                                    String labelText)
            throws InterruptedException {

        WebElement label = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//label[normalize-space()='" + labelText + "']")
                )
        );

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", label);
        Thread.sleep(200);

        try {
            label.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", label);
        }

        Thread.sleep(400);
        System.out.println("Selected radio: " + labelText);
    }

    private void fillInputStable(JavascriptExecutor js, WebElement input, String value)
            throws InterruptedException {

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", input);
        input.click();
        input.clear();

        for (char c : value.toCharArray()) {
            input.sendKeys(String.valueOf(c));
            Thread.sleep(80);
        }

        js.executeScript("arguments[0].dispatchEvent(new Event('input',{bubbles:true}));", input);
        js.executeScript("arguments[0].dispatchEvent(new Event('change',{bubbles:true}));", input);

        Thread.sleep(300);
    }
    private void clickRadioByIndex(List<WebElement> radios,
                                   int index,
                                   JavascriptExecutor js)
            throws InterruptedException {

        WebElement radio = radios.get(index);

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", radio);
        Thread.sleep(200);

        // 🔥 CLICK PARENT, NOT INPUT
        WebElement clickable = radio.findElement(By.xpath(".."));
        js.executeScript("arguments[0].click();", clickable);

        System.out.println("Selected radio index: " + index);
        Thread.sleep(500);
    }

    private void uploadDxf(WebDriver driver, WebDriverWait wait, JavascriptExecutor js,
                           int index, String filePath) throws InterruptedException {

        System.out.println("File path to upload: " + filePath);

        Thread.sleep(1000);

        List<WebElement> fileInputs = driver.findElements(By.cssSelector("input[type='file']"));
        System.out.println("Total file inputs found: " + fileInputs.size());

        if (fileInputs.isEmpty() || index >= fileInputs.size()) {
            System.out.println("ERROR: File input not found at index " + index);
            return;
        }

        WebElement fileInput = fileInputs.get(index);

        js.executeScript(
                "arguments[0].style.cssText = 'display:block !important; visibility:visible !important; opacity:1 !important; position:relative !important; width:100px !important; height:30px !important;';",
                fileInput
        );

        Thread.sleep(500);
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", fileInput);
        Thread.sleep(300);

        try {
            fileInput.sendKeys(filePath);
            System.out.println("File uploaded successfully");
        } catch (Exception e) {
            System.out.println("ERROR uploading file: " + e.getMessage());
        }

        Thread.sleep(1000);
    }

    private void waitForNoOverlay(WebDriver driver, WebDriverWait wait) {
        try {
            java.util.List<By> loaderSelectors = java.util.Arrays.asList(
                    By.cssSelector(".loading"),
                    By.cssSelector(".overlay"),
                    By.cssSelector(".loader"),
                    By.cssSelector(".submit-bar-disabled"),
                    By.cssSelector(".is-loading"),
                    By.cssSelector(".ant-modal-root .ant-spin")
            );
            for (By sel : loaderSelectors) {
                try {
                    wait.until(org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated(sel));
                } catch (Exception ignored) {
                    // not present / timed out -> continue
                }
            }
        } catch (Exception ignored) {}
    }

    /**
     * Helper: try clicking an element multiple times, with a JS fallback.
     * Returns true if clicked.
     */
    private boolean tryClickWithRetries(WebDriver driver, WebDriverWait wait, JavascriptExecutor js, By locator,
                                        int timeoutSeconds, int retries, long retryDelayMs)
            throws InterruptedException {
        WebDriverWait localWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(timeoutSeconds));

        for (int attempt = 1; attempt <= retries; attempt++) {
            try {
                waitForNoOverlay(driver, wait);
                WebElement el = localWait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(locator));
                try {
                    el.click();
                    System.out.println("Clicked element " + locator + " (attempt " + attempt + ")");
                    return true;
                } catch (Exception clickEx) {
                    // fallback to JS click
                    try {
                        js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
                        Thread.sleep(150);
                        js.executeScript("arguments[0].click();", el);
                        System.out.println("JS-clicked element " + locator + " (attempt " + attempt + ")");
                        return true;
                    } catch (Exception jsEx) {
                        System.out.println("Click failed attempt " + attempt + " for " + locator + " : " + jsEx.getMessage());
                    }
                }
            } catch (Exception e) {
                System.out.println("Element not clickable yet (" + locator + ") attempt " + attempt + " : " + e.getMessage());
            }
            Thread.sleep(retryDelayMs);
        }
        return false;
    }
}

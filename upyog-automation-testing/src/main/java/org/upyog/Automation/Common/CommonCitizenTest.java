package org.upyog.Automation.Common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.Automation.Modules.Adv.AdvBookingCreate;
import org.upyog.Automation.Modules.CHB.chbCreate;
import org.upyog.Automation.Modules.EWaste.EWasteCreate;
import org.upyog.Automation.Modules.OBPAS.OBPASCreate;
import org.upyog.Automation.Modules.Pet.PetCreateApplication;
import org.upyog.Automation.Modules.PublicGrievanceRedressal.PgrCreate;
import org.upyog.Automation.Modules.PropertyTax.PropertyTaxCreate;
import org.upyog.Automation.Modules.StreetVending.CreateApplication;
import org.upyog.Automation.Modules.TradeLicense.TradeLicenseCreate;
import org.upyog.Automation.Modules.RequestService.TreePruningCitizen;
import org.upyog.Automation.Modules.RequestService.WaterTankerCitizen;
import org.upyog.Automation.Modules.RequestService.MobileToiletCitizen;
/**
 * Common entry point for all citizen module tests
 * Routes to appropriate module based on moduleName
 */

@Component
public class CommonCitizenTest {

    private static final Logger logger = LoggerFactory.getLogger(CommonCitizenTest.class);

    @Autowired
    private CreateApplication createApplication;
    
    @Autowired
    private TradeLicenseCreate tradeLicenseCreate;
    
    @Autowired
    private PetCreateApplication petCreateApplication;
    
    @Autowired
    private AdvBookingCreate advBookingCreate;
    
    @Autowired
    private TreePruningCitizen treePruningCitizen;
    
    @Autowired
    private WaterTankerCitizen waterTankerCitizen;
    
    @Autowired
    private MobileToiletCitizen mobileToiletCitizen;
    
    @Autowired
    private PropertyTaxCreate propertyTaxCreate;
    
    @Autowired
    private PgrCreate pgrCreate;
    
    @Autowired
    private OBPASCreate obpasCreate;
    
    @Autowired
    private EWasteCreate eWasteCreate;
    
    @Autowired
    private chbCreate chbCreate;

    public void runCitizenTest(String baseUrl, String moduleName, String mobileNumber, String otp, String cityName) {
        logger.info("Starting {} citizen test", moduleName);

        try {
            if ("STREET_VENDING".equalsIgnoreCase(moduleName)) {
                createApplication.svCreateApplication(baseUrl, moduleName, mobileNumber, otp, cityName);

            } else if ("TRADE_LICENSE".equalsIgnoreCase(moduleName)) {
                tradeLicenseCreate.TradeLicenceCitizenReg(baseUrl, moduleName, mobileNumber, otp, cityName);

            } else if ("PET_REGISTRATION".equalsIgnoreCase(moduleName)) {
                petCreateApplication.PetApptest(baseUrl, moduleName, mobileNumber, otp, cityName);

            } else if ("ADVERTISEMENT".equalsIgnoreCase(moduleName)) {
                advBookingCreate.AdvBookingReg(baseUrl, moduleName, mobileNumber, otp, cityName);

            } else if ("TREE_PRUNING".equalsIgnoreCase(moduleName)) {
                treePruningCitizen.TreePruningCreate(baseUrl, moduleName, mobileNumber, otp, cityName);

            } else if ("WATER_TANKER".equalsIgnoreCase(moduleName)) {
                waterTankerCitizen.WaterTankerCreate(baseUrl, moduleName, mobileNumber, otp, cityName);

            } else if ("MOBILE_TOILET".equalsIgnoreCase(moduleName)) {
                mobileToiletCitizen.MobileToiletCreate(baseUrl, moduleName, mobileNumber, otp, cityName);
                
            } else if ("PROPERTY_TAX".equalsIgnoreCase(moduleName)) {
                propertyTaxCreate.NewPropertyReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                
            } else if ("PUBLIC_GRIEVANCE_REDRESSAL".equalsIgnoreCase(moduleName)) {
                pgrCreate.PgrReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                
            } else if ("ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM".equalsIgnoreCase(moduleName)) {
                obpasCreate.OBPASReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                
            } else if ("EWASTE_MANAGEMENT_SYSTEM".equalsIgnoreCase(moduleName)) {
                eWasteCreate.EWasteReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                
            } else if ("COMMUNITY HALL BOOKING".equalsIgnoreCase(moduleName)) {
                chbCreate.chbReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                
            } else {
                logger.error("Unknown module: {}", moduleName);
                throw new RuntimeException("Unknown module: " + moduleName);
            }

            logger.info("{} test completed", moduleName);

        } catch (Exception e) {
            logger.error("Error in {} test: {}", moduleName, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

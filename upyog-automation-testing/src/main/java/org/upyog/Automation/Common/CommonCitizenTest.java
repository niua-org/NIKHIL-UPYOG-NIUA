package org.upyog.Automation.Common;

import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.Automation.Modules.Adv.AdvBookingCreate;
import org.upyog.Automation.Modules.CHB.chbCreate;
import org.upyog.Automation.Modules.CnD.CnDRequest;
import org.upyog.Automation.Modules.DesludgingService.DesludgingCreate;
import org.upyog.Automation.Modules.EWaste.EWasteCreate;
import org.upyog.Automation.Modules.OBPAS.OBPASCreate;
import org.upyog.Automation.Modules.OBPAS.OBPASOcCreate;
import org.upyog.Automation.Modules.Pet.PetCreateApplication;
import org.upyog.Automation.Modules.PublicGrievanceRedressal.PgrCreate;
import org.upyog.Automation.Modules.PropertyTax.PropertyTaxCreate;
import org.upyog.Automation.Modules.StreetVending.CreateApplication;
import org.upyog.Automation.Modules.TradeLicense.TradeLicenseCreate;
import org.upyog.Automation.Modules.RequestService.TreePruningCitizen;
import org.upyog.Automation.Modules.RequestService.WaterTankerCitizen;
import org.upyog.Automation.Modules.RequestService.MobileToiletCitizen;
import org.upyog.Automation.Modules.WaterAndSewerage.WAndSCreate;
import org.upyog.Automation.Modules.DesludgingService.DesludgingCreate;
import org.upyog.Automation.config.WebDriverFactory;


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

    @Autowired
    private CnDRequest cndRequest;

    @Autowired
    private OBPASOcCreate obpasOcCreate;

    @Autowired
    private DesludgingCreate desludgingCreate;

    @Autowired
    private WAndSCreate wAndSCreate;

    public void runCitizenTest(String baseUrl, String moduleName, String mobileNumber, String otp, String cityName, String permitNumber) throws InterruptedException {
        logger.info("Starting {} citizen test", moduleName);

        try {
            switch (moduleName.toUpperCase()) {

                case "STREET_VENDING":
                    createApplication.svCreateApplication(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "TRADE_LICENSE":
                    tradeLicenseCreate.TradeLicenceCitizenReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "PET_REGISTRATION":
                    petCreateApplication.PetApptest(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "ADVERTISEMENT":
                    advBookingCreate.AdvBookingReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "TREE_PRUNING":
                    treePruningCitizen.TreePruningCreate(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "WATER_TANKER":
                    waterTankerCitizen.WaterTankerCreate(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "MOBILE_TOILET":
                    mobileToiletCitizen.MobileToiletCreate(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "PROPERTY_TAX":
                    propertyTaxCreate.NewPropertyReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "PUBLIC_GRIEVANCE_REDRESSAL":
                    pgrCreate.PgrReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM":
                    obpasCreate.OBPASReg(baseUrl, moduleName, mobileNumber, otp, cityName, permitNumber);
                    break;

                case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC":
                    obpasOcCreate.OBPASOCReg(baseUrl, moduleName, mobileNumber, otp, cityName, permitNumber);
                    break;

                case "EWASTE_MANAGEMENT_SYSTEM":
                    eWasteCreate.EWasteReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "COMMUNITY_HALL_BOOKING":
                    chbCreate.chbReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "CONSTRUCTION_AND_DEMOLITION":
                    cndRequest.CndReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "DESLUDGING_SERVICE":
                    desludgingCreate.desludgingReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "WATER_AND_SEWERAGE":
                    wAndSCreate.WandSReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                default:
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
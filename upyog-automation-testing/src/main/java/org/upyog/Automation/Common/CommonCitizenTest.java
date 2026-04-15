package org.upyog.Automation.Common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.Automation.Modules.Adv.AdvBookingCreate;
import org.upyog.Automation.Modules.CHB.chbCreate;
import org.upyog.Automation.Modules.CnD.CnDRequest;
import org.upyog.Automation.Modules.DesludgingService.DesludgingCreate;
import org.upyog.Automation.Modules.EWaste.EWasteCreate;
import org.upyog.Automation.Modules.FireNoc.FireRequest;
import org.upyog.Automation.Modules.OBPAS.OBPASCreate;
import org.upyog.Automation.Modules.Pet.PetCreateApplication;
import org.upyog.Automation.Modules.PublicGrievanceRedressal.PgrCreate;
import org.upyog.Automation.Modules.PropertyTax.PropertyTaxCreate;
import org.upyog.Automation.Modules.StreetVending.CreateApplication;
import org.upyog.Automation.Modules.TradeLicense.TradeLicenseCreate;
import org.upyog.Automation.Modules.RequestService.TreePruningCitizen;
import org.upyog.Automation.Modules.RequestService.WaterTankerCitizen;
import org.upyog.Automation.Modules.RequestService.MobileToiletCitizen;
import org.upyog.Automation.Modules.WaterAndSewerage.WAndSCreate;

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
            switch (moduleName.toUpperCase()) {

                case "STREET_VENDING":
                    CreateApplication svApp = new CreateApplication();
                    svApp.svCreateApplication(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "TRADE_LICENSE":
                    TradeLicenseCreate tlApp = new TradeLicenseCreate();
                    tlApp.TradeLicenceCitizenReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "PET_REGISTRATION":
                    PetCreateApplication petApp = new PetCreateApplication();
                    petApp.PetApptest(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "ADVERTISEMENT":
                    AdvBookingCreate advApp = new AdvBookingCreate();
                    advApp.AdvBookingReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "TREE_PRUNING":
                    TreePruningCitizen treePruningApp = new TreePruningCitizen();
                    treePruningApp.TreePruningCreate(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "WATER_TANKER":
                    WaterTankerCitizen waterTankerApp = new WaterTankerCitizen();
                    waterTankerApp.WaterTankerCreate(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "MOBILE_TOILET":
                    MobileToiletCitizen mobileToiletApp = new MobileToiletCitizen();
                    mobileToiletApp.MobileToiletCreate(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "PROPERTY_TAX":
                    PropertyTaxCreate propertyTaxApp = new PropertyTaxCreate();
                    propertyTaxApp.NewPropertyReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "PUBLIC_GRIEVANCE_REDRESSAL":
                    PgrCreate pgrApp = new PgrCreate();
                    pgrApp.PgrReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM":
                    OBPASCreate obpasApp = new OBPASCreate();
                    obpasApp.OBPASReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "EWASTE_MANAGEMENT_SYSTEM":
                    EWasteCreate eWasteApp = new EWasteCreate();
                    eWasteApp.EWasteReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "COMMUNITY_HALL_BOOKING":
                    chbCreate chbApp = new chbCreate();
                    chbApp.chbReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "CONSTRUCTION_AND_DEMOLITION":
                    CnDRequest cndApp = new  CnDRequest();
                    cndApp.CndReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "DESLUDGING_SERVICE":
                    DesludgingCreate desludgingApp = new  DesludgingCreate();
                    desludgingApp.desludgingReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "WATER_AND_SEWERAGE":
                    WAndSCreate WAndSApp = new  WAndSCreate();
                    WAndSApp.WandSReg(baseUrl, moduleName, mobileNumber, otp, cityName);
                    break;

                case "FIRE_NOC":
                    FireRequest fireApp = new FireRequest();
                    fireApp.fireReg(baseUrl, moduleName, mobileNumber, otp, cityName);
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
    }}
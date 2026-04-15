package org.upyog.Automation.Common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.Automation.Modules.Adv.AdvEmp;
import org.upyog.Automation.Modules.Asset.AssetEmp;
import org.upyog.Automation.Modules.CnD.CnDEmp;
import org.upyog.Automation.Modules.EWaste.EWasteEmp;
import org.upyog.Automation.Modules.OBPAS.OBPASEmp;
import org.upyog.Automation.Modules.Pet.PetApplicationEmp;
import org.upyog.Automation.Modules.PropertyTax.PropertyTaxEmp;
import org.upyog.Automation.Modules.PublicGrievanceRedressal.PgrEmp;
import org.upyog.Automation.Modules.RequestService.MobileToiletEmp;
import org.upyog.Automation.Modules.RequestService.TreePruningEmp;
import org.upyog.Automation.Modules.RequestService.TreePruningVerifier;
import org.upyog.Automation.Modules.RequestService.WaterTankerEmployee;
import org.upyog.Automation.Modules.StreetVending.SvEmp;
import org.upyog.Automation.Modules.TradeLicense.TradeLicenseEmp;
import org.upyog.Automation.Modules.CHB.chbEmp;
import org.upyog.Automation.Modules.WaterAndSewerage.SewerageEmp;
import org.upyog.Automation.Modules.WaterAndSewerage.WaterEmp;

/**
 * Common entry point for all employee module tests
 * Routes to appropriate module based on moduleName
 */
@Component
public class CommonEmployeeTest {

    private static final Logger logger = LoggerFactory.getLogger(CommonEmployeeTest.class);

    @Autowired
    private SvEmp svEmp;
    
    @Autowired
    private PetApplicationEmp petApplicationEmp;
    
    @Autowired
    private TradeLicenseEmp tradeLicenseEmp;
    
    @Autowired
    private AssetEmp assetEmp;

    public void runEmployeeTest(String baseUrl, String moduleName, String username, String password, String applicationNumber) {
        logger.info("Starting {} employee test", moduleName);

        try {
            switch (moduleName.toUpperCase()) {


                case "STREET_VENDING":
                    new SvEmp().InboxEmpSv(baseUrl, username, password, applicationNumber);
                    break;

                case "PET_REGISTRATION":
                    new PetApplicationEmp().petInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "TRADE_LICENSE":
                    new TradeLicenseEmp().tlInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "ASSET_MANAGEMENT_SYSTEM":
                    new AssetEmp().assetInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "ADVERTISEMENT":
                    new AdvEmp().AdvInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "PROPERTY_TAX":
                    new PropertyTaxEmp().PropertyInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "EWASTE_MANAGEMENT_SYSTEM":
                    new EWasteEmp().EWasteInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM":
                    new OBPASEmp().OBPASInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "WATER_TANKER":
                    new WaterTankerEmployee().WaterTankerInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "TREE_PRUNING":
                    new TreePruningEmp().TreePruningInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "TREE_PRUNING_VERIFIER":
                    new TreePruningVerifier().TreePruningInboxVerifier(baseUrl, username, password, applicationNumber);
                    break;

                case "MOBILE_TOILET":
                    new MobileToiletEmp().MobileToiletInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "COMMUNITY_HALL_BOOKING":
                    new chbEmp().chbInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "CONSTRUCTION_AND_DEMOLITION":
                    new CnDEmp().CnDInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "PUBLIC_GRIEVANCE_REDRESSAL":
                    new PgrEmp().PgrInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "WATER_AND_SEWERAGE":
                    if (applicationNumber.startsWith("SW")) {
                        new SewerageEmp().SewerageInboxEmp(baseUrl, username, password, applicationNumber);
                    } else {
                        new WaterEmp().WaterInboxEmp(baseUrl, username, password, applicationNumber);
                    }
                    break;

                default:
                    logger.error("Unknown module: {}", moduleName);
                    throw new RuntimeException("Unknown module: " + moduleName);
            }

            logger.info("{} employee test completed", moduleName);

        } catch (Exception e) {
            logger.error("Error in {} employee test: {}", moduleName, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
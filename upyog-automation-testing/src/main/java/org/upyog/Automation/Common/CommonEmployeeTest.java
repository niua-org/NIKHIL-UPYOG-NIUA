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
import org.upyog.Automation.Modules.OBPAS.OBPASOcEmp;
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

    @Autowired
    private AdvEmp advEmp;

    @Autowired
    private PropertyTaxEmp propertyTaxEmp;

    @Autowired
    private EWasteEmp eWasteEmp;

    @Autowired
    private OBPASEmp oBPASEmp;

    @Autowired
    private OBPASOcEmp oBPASOcEmp;

    @Autowired
    private WaterTankerEmployee waterTankerEmployee;

    @Autowired
    private TreePruningEmp treePruningEmp;

    @Autowired
    private TreePruningVerifier treePruningVerifier;

    @Autowired
    private MobileToiletEmp mobileToiletEmp;

    @Autowired
    private chbEmp chbEmp;

    @Autowired
    private CnDEmp cnDEmp;

    @Autowired
    private PgrEmp pgrEmp;

    @Autowired
    private SewerageEmp sewerageEmp;

    @Autowired
    private WaterEmp waterEmp;



    public void runEmployeeTest(String baseUrl, String moduleName, String username, String password, String applicationNumber) {
        logger.info("Starting {} employee test", moduleName);

        try {
            switch (moduleName.toUpperCase()) {


                case "STREET_VENDING":
                    svEmp.InboxEmpSv(baseUrl, username, password, applicationNumber);
                    break;

                case "PET_REGISTRATION":
                    petApplicationEmp.petInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "TRADE_LICENSE":
                    tradeLicenseEmp.tlInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "ASSET_MANAGEMENT_SYSTEM":
                    assetEmp.assetInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "ADVERTISEMENT":
                    advEmp.AdvInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "PROPERTY_TAX":
                    propertyTaxEmp.PropertyInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "EWASTE_MANAGEMENT_SYSTEM":
                    eWasteEmp.EWasteInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM":
                    oBPASEmp.OBPASInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC":
                    oBPASOcEmp.OBPASOcInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "WATER_TANKER":
                    waterTankerEmployee.WaterTankerInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "TREE_PRUNING":
                    treePruningEmp.TreePruningInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "TREE_PRUNING_VERIFIER":
                    treePruningVerifier.TreePruningInboxVerifier(baseUrl, username, password, applicationNumber);
                    break;

                case "MOBILE_TOILET":
                    mobileToiletEmp.MobileToiletInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "COMMUNITY_HALL_BOOKING":
                    chbEmp.chbInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "CONSTRUCTION_AND_DEMOLITION":
                    cnDEmp.CnDInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "PUBLIC_GRIEVANCE_REDRESSAL":
                    pgrEmp.PgrInboxEmp(baseUrl, username, password, applicationNumber);
                    break;

                case "WATER_AND_SEWERAGE":
                    if (applicationNumber.startsWith("SW")) {
                        sewerageEmp.SewerageInboxEmp(baseUrl, username, password, applicationNumber);
                    } else {
                        waterEmp.WaterInboxEmp(baseUrl, username, password, applicationNumber);
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
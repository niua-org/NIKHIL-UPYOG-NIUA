package org.upyog.Automation.Common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.Automation.Modules.Asset.AssetEmp;
import org.upyog.Automation.Modules.Pet.PetApplicationEmp;
import org.upyog.Automation.Modules.StreetVending.SvEmp;
import org.upyog.Automation.Modules.TradeLicense.TradeLicenseEmp;

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
            if ("STREET_VENDING".equalsIgnoreCase(moduleName)) {
                svEmp.InboxEmpSv(baseUrl, username, password, applicationNumber);

            } else if ("PET_REGISTRATION".equalsIgnoreCase(moduleName)) {
                petApplicationEmp.petInboxEmp(baseUrl, username, password, applicationNumber);

            } else if ("TRADE_LICENSE".equalsIgnoreCase(moduleName)) {
                tradeLicenseEmp.tlInboxEmp(baseUrl, username, password, applicationNumber);

            } else if ("ASSET_MANAGEMENT_SYSTEM".equalsIgnoreCase(moduleName)) {
                assetEmp.assetInboxEmp(baseUrl, username, password, applicationNumber);

            } else {
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

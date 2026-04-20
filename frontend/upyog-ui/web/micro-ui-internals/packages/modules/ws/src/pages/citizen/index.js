import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Routes, useLocation, Route } from "react-router-dom";
import { PrivateRoute, BackButton } from "@upyog/digit-ui-react-components";
import TestAcknowledgment from "./TestAcknowledgment";
import { WSMyApplications } from "./WSMyApplications";

const App = ({ path }) => {
  const location = useLocation();
  const { t } = useTranslation();
  let isCommonPTPropertyScreen = window.location.href.includes("/ws/create-application/property-details");
  let isAcknowledgement = window.location.href.includes("/acknowledgement") || window.location.href.includes("/disconnect-acknowledge");
  const WSDisconnectAcknowledgement = Digit?.ComponentRegistryService?.getComponent("WSDisconnectAcknowledgement");
  const WSRestorationAcknowledgement = Digit?.ComponentRegistryService?.getComponent("WSRestorationAcknowledgement");
  const getBackPageNumber = () => {
    let goBacktoFromProperty = -1;
    if (sessionStorage.getItem("VisitedCommonPTSearch") === "true" && isCommonPTPropertyScreen) {
      goBacktoFromProperty = -4;
      return goBacktoFromProperty;
    }
    return goBacktoFromProperty;
  };

  const WSCreate = Digit?.ComponentRegistryService?.getComponent("WSCreate");
  const WSDisconnection = Digit?.ComponentRegistryService?.getComponent("WSDisconnection");
  const WSRestoration = Digit?.ComponentRegistryService?.getComponent("WSRestoration");
  const WSSearchConnectionComponent = Digit?.ComponentRegistryService?.getComponent("WSSearchConnectionComponent");
  const WSSearchResultsComponent = Digit?.ComponentRegistryService?.getComponent("WSSearchResultsComponent");
  const WSCitizenApplicationDetails = Digit?.ComponentRegistryService?.getComponent("WSCitizenApplicationDetails");
  const WSAdditionalDetails = Digit?.ComponentRegistryService?.getComponent("WSAdditionalDetails");
  const WSCitizenConnectionDetails = Digit?.ComponentRegistryService?.getComponent("WSCitizenConnectionDetails");
  const WSCitizenConsumptionDetails = Digit?.ComponentRegistryService?.getComponent("WSCitizenConsumptionDetails");
  const WSMyPayments = Digit?.ComponentRegistryService?.getComponent("WSMyPayments");
  const WSCitizenEditApplication = Digit?.ComponentRegistryService?.getComponent("WSCitizenEditApplication");
  const WSReSubmitDisconnectionApplication = Digit?.ComponentRegistryService?.getComponent("WSReSubmitDisconnectionApplication");
  const WSMyConnections = Digit?.ComponentRegistryService?.getComponent("WSMyConnections");
  const WNSMyBillsComponent = Digit?.ComponentRegistryService?.getComponent("WNSMyBillsComponent");
  return (
    <React.Fragment>
      <div className="ws-citizen-wrapper">
        {!isAcknowledgement && <BackButton style={{ border: "none" }} /* isCommonPTPropertyScreen={isCommonPTPropertyScreen} */ getBackPageNumber={getBackPageNumber}>
          {t("CS_COMMON_BACK")}
        </BackButton>}
        <Routes>
          <Route path={`${path}/create-application/*`} element={<PrivateRoute><WSCreate /></PrivateRoute>} />
          <Route path={`${path}/disconnect-application/*`} element={<PrivateRoute><WSDisconnection /></PrivateRoute>} />
          <Route path={`${path}/restore-application/*`} element={<PrivateRoute><WSRestoration /></PrivateRoute>} />
          <Route path={`${path}/disconnect-acknowledge`} element={<PrivateRoute><WSDisconnectAcknowledgement /></PrivateRoute>} />
          <Route path={`${path}/restoration-acknowledge`} element={<PrivateRoute><WSRestorationAcknowledgement /></PrivateRoute>} />
          <Route path={`${path}/resubmit-disconnect-application/*`} element={<PrivateRoute><WSReSubmitDisconnectionApplication /></PrivateRoute>} />
          <Route path={`${path}/search`} element={<WSSearchConnectionComponent />} />
          <Route path={`${path}/my-bills`} element={<PrivateRoute><WNSMyBillsComponent /></PrivateRoute>} />
          <Route path={`${path}/search-results`} element={<WSSearchResultsComponent />} />
          <Route path={`${path}/test-acknowledgment`} element={<TestAcknowledgment />} />
          <Route path={`${path}/my-payments`} element={<PrivateRoute><WSMyPayments /></PrivateRoute>} />
          <Route path={`${path}/my-applications`} element={<PrivateRoute><WSMyApplications /></PrivateRoute>} />
          <Route path={`${path}/my-connections`} element={<PrivateRoute><WSMyConnections /></PrivateRoute>} />
          <Route path={`${path}/connection/application/:acknowledgementIds`} element={<PrivateRoute><WSCitizenApplicationDetails /></PrivateRoute>} />
          <Route path={`${path}/connection/additional/:acknowledgementIds`} element={<PrivateRoute><WSAdditionalDetails /></PrivateRoute>} />
          <Route path={`${path}/connection/details/:acknowledgementIds`} element={<PrivateRoute><WSCitizenConnectionDetails /></PrivateRoute>} />
          <Route path={`${path}/consumption/details`} element={<PrivateRoute><WSCitizenConsumptionDetails /></PrivateRoute>} />
          <Route path={`${path}/edit-application/:tenantId`} element={<PrivateRoute><WSCitizenEditApplication /></PrivateRoute>} />
          <Route path={`${path}/modify-connection/:tenantId`} element={<PrivateRoute><WSCitizenEditApplication /></PrivateRoute>} />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default App;

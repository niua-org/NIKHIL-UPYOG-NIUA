import { AppContainer, BackButton, PrivateRoute } from "@upyog/digit-ui-react-components";
import React from "react";
import { Route, Routes } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";

const hideBackButtonConfig = [];

const App = () => {
  const { path, url, ...match } = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();

  const EWCreate = Digit?.ComponentRegistryService?.getComponent("EWCreatewaste");
  const EWASTEMyApplications = Digit?.ComponentRegistryService?.getComponent("EWASTEMyApplications");
  const EWASTEApplicationDetails = Digit?.ComponentRegistryService?.getComponent("EWASTECitizenApplicationDetails");
 
  return (
    <span className={"citizen"} style={{ width: "100%" }}>
      <AppContainer>
        {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
        <Routes>
          <Route path={`${path}/raiseRequest`} element={<PrivateRoute><EWCreate /></PrivateRoute>} />
          <Route path={`${path}/application/:requestId/:tenantId`} element={<PrivateRoute><EWASTEApplicationDetails /></PrivateRoute>} />
          <Route path={`${path}/myApplication`} element={<PrivateRoute><EWASTEMyApplications /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;
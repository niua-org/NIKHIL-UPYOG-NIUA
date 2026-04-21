import { AppContainer, BackButton, PrivateRoute } from "@upyog/digit-ui-react-components";
import React from "react";
import { Routes, Route } from "react-router-dom";
import { useTranslation } from "react-i18next";


const App = () => {
  const { path, url, ...match } = Digit.Hooks.useModuleBasePath();
  const SVCreate = Digit?.ComponentRegistryService?.getComponent("Create");
  const MyApplication = Digit?.ComponentRegistryService?.getComponent("SVMyApplications");
  const SvApplicationDetails = Digit?.ComponentRegistryService?.getComponent("SvApplicationDetails");
  return (
    <span className={"sv-citizen"}style={{width:"100%"}}>
      <AppContainer>
        <BackButton>Back</BackButton>
        <Routes>
          <Route path={`${path}/apply`} element={<PrivateRoute><SVCreate /></PrivateRoute>} />
          <Route path={`${path}/edit`} element={<PrivateRoute><SVCreate /></PrivateRoute>} />
          <Route path={`${path}/my-applications`} element={<PrivateRoute><MyApplication /></PrivateRoute>} />
          <Route path={`${path}/application/:applicationNo/:tenantId`} element={<PrivateRoute><SvApplicationDetails /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;
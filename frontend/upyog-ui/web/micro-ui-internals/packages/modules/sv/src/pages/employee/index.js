import { AppContainer, BackButton, PrivateRoute, BreadCrumb } from "@upyog/digit-ui-react-components";
import React from "react";
import { Route, useLocation, Routes } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Inbox from "./Inbox";
import SearchApp from "./SearchApp";

/** The Main routes component for the employee side
 * Contains routes for every page there is to redirect in the employee side
 * Contains breadcrumbs for each page
 */
const EmployeeApp = () => {
  const { path, url, ...match } = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();
  const location = useLocation();
  const isMobile = false

  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["street-vending"],
      status: null,
      vendingType: null,
      vendingZone: null
    },
  };

 
  const SVEmpCreate = Digit?.ComponentRegistryService?.getComponent("SVEmpCreate");
  const SVApplicationDetails = Digit?.ComponentRegistryService?.getComponent("SVApplicationDetails")
  return (
    <span className={"sv-citizen"}style={{width:"100%"}}>
      <AppContainer>
        <BackButton style={{marginTop:"15px"}}>Back</BackButton>
        <Routes>
          <Route path={`${path}/apply`} element={<PrivateRoute><SVEmpCreate /></PrivateRoute>} />
          <Route
            path={`${path}/inbox`}
            element={
              <PrivateRoute>
                <Inbox
                  useNewInboxAPI={true}
                  parentRoute={path}
                  businessService="sv"
                  filterComponent="SV_INBOX_FILTER"
                  initialStates={inboxInitialState}
                  isInbox={true}
                />
              </PrivateRoute>
            }
          />
          <Route path={`${path}/application-details/:id`} element={<PrivateRoute><SVApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/applicationsearch/application-details/:id`} element={<PrivateRoute><SVApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/my-applications`} element={<PrivateRoute><SearchApp parentRoute={path} /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default EmployeeApp;
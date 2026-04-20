import { AppContainer, BackButton, PrivateRoute, BreadCrumb } from "@upyog/digit-ui-react-components";
import React from "react";
import { Route, useLocation, Routes } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Inbox from "./Inbox";

/** The Main routes component for the employee side
 * Contains routes for every page there is to redirect in the employee side
 * Contains breadcrumbs for each page
 */
const EmployeeApp = () => {
  const { path, url, ...match } = Digit.Hooks.useModuleBasePath();
  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["PGRAI"],
    },
  };

 
  const ComplaintDetails = Digit?.ComponentRegistryService?.getComponent("PGRAIApplicationDetails");
  const PGRAICreate = Digit?.ComponentRegistryService?.getComponent("PGRAICreate");
  return (
    <span className={"pgr-ai-citizen"} style={{ width: "50%" }}>
      <AppContainer>
        <BackButton style={{ marginTop: "15px" }}>Back</BackButton>
        <Routes>
          <Route path={`${path}/complaint/create`} element={<PrivateRoute><PGRAICreate /></PrivateRoute>} />
          <Route
            path={`${path}/inbox`}
            element={
              <PrivateRoute>
                <Inbox
                  useNewInboxAPI={true}
                  parentRoute={path}
                  businessService="pgrai"
                  filterComponent="PGRAI_INBOX_FILTER"
                  initialStates={inboxInitialState}
                  isInbox={true}
                />
              </PrivateRoute>
            }
          />
          <Route path={`${path}/complaint-details/:id`} element={<PrivateRoute><ComplaintDetails parentRoute={path} /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default EmployeeApp;
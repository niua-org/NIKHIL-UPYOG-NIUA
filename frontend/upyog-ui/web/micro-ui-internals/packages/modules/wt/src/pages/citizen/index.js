import { AppContainer, BackButton, PrivateRoute } from "@upyog/digit-ui-react-components";
import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { APPLICATION_PATH } from "../../utils";
import SearchApp from "../employee/SearchApp";

// Main Routing Page used for routing accorss the Water Tanker Module
const App = () => {
  const { path, url, ...match } = Digit.Hooks.useModuleBasePath();
  const WTCreate = Digit?.ComponentRegistryService?.getComponent("WTCreate");
  const WTApplicationDetails = Digit?.ComponentRegistryService?.getComponent("WTApplicationDetails");
  const MTApplicationDetails = Digit?.ComponentRegistryService?.getComponent("MTApplicationDetails");
  const TPApplicationDetails = Digit?.ComponentRegistryService?.getComponent("TPApplicationDetails");
  const WTMyApplications = Digit?.ComponentRegistryService?.getComponent("WTMyApplications");
  const Inbox = Digit.ComponentRegistryService.getComponent("WTEmpInbox");
  const WTCard = Digit.ComponentRegistryService.getComponent("WTCitizenCard");
  const MTCard = Digit.ComponentRegistryService.getComponent("MTCitizenCard");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");

  const getInboxInitialState = (service) => ({
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: [service],
      applicationStatus: [],
      locality: [],
    },
  });
  // Initial state for waterTanker inbox and mobileToilet inbox
  const inboxInitialStateWT = getInboxInitialState("watertanker");
  const inboxInitialStateMT = getInboxInitialState("mobileToilet");

  return (
    <span style={{ width: "100%" }}>
      <AppContainer>
        <BackButton>Back</BackButton>
        <Routes>
          <Route
            path={`${path}/inbox`}
            element={
              <PrivateRoute>
                <Inbox
                  useNewInboxAPI={true}
                  parentRoute={path}
                  moduleCode="WT"
                  businessService="watertanker"
                  filterComponent="WT_INBOX_FILTER"
                  initialStates={inboxInitialStateWT}
                  isInbox={true}
                />
              </PrivateRoute>
            }
          />
          <Route
            path={`${path}/mt/inbox`}
            element={
              <PrivateRoute>
                <Inbox
                  useNewInboxAPI={true}
                  parentRoute={path}
                  businessService="mobileToilet"
                  moduleCode="MT"
                  filterComponent="WT_INBOX_FILTER"
                  initialStates={inboxInitialStateMT}
                  isInbox={true}
                />
              </PrivateRoute>
            }
          />
          <Route path={`${path}/request-service`} element={<PrivateRoute><WTCreate /></PrivateRoute>} />
          <Route path={`${path}/status`} element={<PrivateRoute><WTMyApplications /></PrivateRoute>} />
          <Route path={`${path}/booking/waterTanker/:acknowledgementIds/:tenantId`} element={<PrivateRoute><WTApplicationDetails /></PrivateRoute>} />
          <Route path={`${path}/booking/mobileToilet/:acknowledgementIds/:tenantId`} element={<PrivateRoute><MTApplicationDetails /></PrivateRoute>} />
          <Route path={`${path}/booking/treePruning/:acknowledgementIds/:tenantId`} element={<PrivateRoute><TPApplicationDetails /></PrivateRoute>} />
          <Route path={`${path}/booking-details/:id`} element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/bookingsearch/booking-details/:id`} element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route
            path={`${path}/wt-Vendor`}
            element={
              Digit.UserService.hasAccess(["WT_VENDOR"]) ? (
                <PrivateRoute>
                  <WTCard parentRoute={path} />
                </PrivateRoute>
              ) : (
                <Navigate
                  to={{
                    pathname: `${APPLICATION_PATH}/citizen/login`,
                    state: { from: `${path}/wt-Vendor`, role: "WT_VENDOR" },
                  }}
                  replace
                />
              )
            }
          />
          <Route
            path={`${path}/mt-Vendor`}
            element={
              Digit.UserService.hasAccess(["MT_VENDOR"]) ? (
                <PrivateRoute>
                  <MTCard parentRoute={path} />
                </PrivateRoute>
              ) : (
                <Navigate
                  to={{
                    pathname: `${APPLICATION_PATH}/citizen/login`,
                    state: { from: `${path}/mt-Vendor`, role: "MT_VENDOR" },
                  }}
                  replace
                />
              )
            }
          />
          <Route path={`${path}/my-bookings`} element={<PrivateRoute><SearchApp parentRoute={path} moduleCode={"WT"} /></PrivateRoute>} />
          <Route path={`${path}/mt/my-bookings`} element={<PrivateRoute><SearchApp parentRoute={path} moduleCode={"MT"} /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;

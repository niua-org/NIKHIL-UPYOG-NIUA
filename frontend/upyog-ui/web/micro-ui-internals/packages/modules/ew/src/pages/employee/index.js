import { PrivateRoute,BreadCrumb } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useLocation, Routes, Route } from "react-router-dom";
import Inbox from "./Inbox";
import SearchApp from "./SearchApp";


const EmployeeApp = ({ path, url, userType }) => {
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  sessionStorage.removeItem("revalidateddone");
  const isMobile = window.Digit.Utils.browser.isMobile();

  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["ewst"],
      applicationStatus: [],
      locality: [],

    },
  };

  const searchMW = [];


  const EWBreadCrumbs = ({ location }) => {
    const { t } = useTranslation();
    const search = useLocation().search;
    const fromScreen = new URLSearchParams(search).get("from") || null;
    const { from : fromScreen2 } = Digit.Hooks.useQueryParams();
    const crumbs = [
      {
        path: "/upyog-ui/employee",
        content: t("ES_COMMON_HOME"),
        show: true,
      },
      {
        path: "/upyog-ui/employee/ew/inbox",
        content: t("ES_TITLE_INBOX"),
        show: location.pathname.includes("ew/inbox") ? true : false,
      },
     
    
      {
        path: "/upyog-ui/employee/my-applications",
        content: t("ES_COMMON_APPLICATION_SEARCH"),
        show: location.pathname.includes("/ew/my-applications") || location.pathname.includes("/ew/application-details") ? true : false,
      },
      
     
      
    ];
  
    return <BreadCrumb style={isMobile?{display:"flex"}:{}}  spanStyle={{maxWidth:"min-content"}} crumbs={crumbs} />;
  }
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("EWApplicationDetails");

  return (
    <React.Fragment>
      <div className="ground-container">
        <div style={{ marginLeft: "12px" }}>
          <EWBreadCrumbs location={location} />
        </div>
        <Routes>
          <Route
            path={`${path}/inbox`}
            element={
              <PrivateRoute>
                <Inbox
                  useNewInboxAPI={true}
                  parentRoute={path}
                  businessService="ewst"
                  filterComponent="EW_INBOX_FILTER"
                  initialStates={inboxInitialState}
                  isInbox={true}
                />
              </PrivateRoute>
            }
          />
          <Route path={`${path}/application-details/:id`} element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/applicationsearch/application-details/:id`} element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/search`} element={<PrivateRoute><SearchApp path={`${path}/search`} /></PrivateRoute>} />
          <Route
            path={`${path}/searchold`}
            element={
              <PrivateRoute>
                <Inbox
                  parentRoute={path}
                  businessService="ewst"
                  middlewareSearch={searchMW}
                  initialStates={inboxInitialState}
                  isInbox={false}
                  EmptyResultInboxComp={"PTEmptyResultInbox"}
                />
              </PrivateRoute>
            }
          />
          <Route path={`${path}/my-applications`} element={<PrivateRoute><SearchApp path={`${path}/my-applications`} /></PrivateRoute>} />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default EmployeeApp;

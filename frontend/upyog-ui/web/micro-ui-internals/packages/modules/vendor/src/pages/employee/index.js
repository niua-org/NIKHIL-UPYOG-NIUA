import { PrivateRoute,BreadCrumb,AppContainer,BackButton, CloseSvg } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation, Routes, Route } from "react-router-dom";
import SearchApp from "./SearchApp";

export const VendorBreadCrumb = ({ location }) => {
  const { t } = useTranslation();
  const isVendor = location?.pathname?.includes("vendor");
  const isSearchVendor = location?.pathname?.includes("search-vendor");
  const isRegistry = location?.pathname?.includes("registry");
  const isNewVendor = location?.pathname?.includes("new-vendor");
  const isNewVehicle = location?.pathname?.includes("new-vehicle");
  const isNewDriver = location?.pathname?.includes("new-driver");

  const crumbs = [
    {
      path: "/upyog-ui/employee",
      content: t("ES_COMMON_HOME"),
      show: isVendor,
    },
    {
      path: "/upyog-ui/employee/vendor/search-vendor?selectedTabs=VENDOR",
      content: "VENDOR",
      show: isVendor,
    },
    {
      content: isNewVendor
        ? t("ES_FSM_REGISTRY_TITLE_NEW_VENDOR")
        : isNewVehicle
        ? t("ES_FSM_REGISTRY_TITLE_NEW_VEHICLE")
        : isNewDriver
        ? t("ES_FSM_REGISTRY_TITLE_NEW_DRIVER")
        : null,
      show: isRegistry && (isNewVendor || isNewVehicle || isNewDriver),
    },
  ];

  return <BreadCrumb crumbs={crumbs} />;
};

const EmployeeApp = ({ path, url, userType }) => {
  console.log("tttttttttttt",path)
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  sessionStorage.removeItem("revalidateddone");
  const isMobile = window.Digit.Utils.browser.isMobile();

  const inboxInitialState = {
    // searchParams: {
    //   uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
    //   services: ["asset-create"],
    //   applicationStatus: [],
    //   locality: [],

    // },
  };


  console.log("index page in employee")
  //const Create = Digit?.ComponentRegistryService?.getComponent("VENDOREMPCreate");
  const AddVendor = Digit.ComponentRegistryService.getComponent("AddVendor");
  const SearchVendor = Digit.ComponentRegistryService.getComponent("SearchVendor");
  //const SearchApp = Digit.ComponentRegistryService.getComponent("SearchApp");
  const AddDriver = Digit.ComponentRegistryService.getComponent("AddDriver");
  const EditVendorDetails = Digit.ComponentRegistryService.getComponent("EditVendorDetails");
  const AddVehicle = Digit.ComponentRegistryService.getComponent("AddVehicle");
  const VendorCreate =  Digit.ComponentRegistryService.getComponent("VENDORCreate");
  const DriverDetails = Digit.ComponentRegistryService.getComponent("DriverDetails");
  const VehicleDetails = Digit.ComponentRegistryService.getComponent("VehicleDetails");

  return (
    <AppContainer>
      <div className="ground-container">
        <div style={{ marginLeft: "-4px" }}>
          <VendorBreadCrumb location={location} />
        </div>
        <Routes>
          <Route path={`${path}/registry/new-vendor`} element={<PrivateRoute><AddVendor parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/search-vendor`} element={<PrivateRoute><SearchVendor parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/registry/new-driver`} element={<PrivateRoute><AddDriver parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/registry/vendor-details/:id`} element={<PrivateRoute><EditVendorDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/registry/vehicle-details/:id`} element={<PrivateRoute><VehicleDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/registry/new-vehicle`} element={<PrivateRoute><AddVehicle parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/registry/additionaldetails`} element={<PrivateRoute><VendorCreate parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/registry/driver-details`} element={<PrivateRoute><DriverDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/common-search/:id`} element={<PrivateRoute><SearchApp parentRoute={path} /></PrivateRoute>} />
          <Route path={`${path}/new-application`} element={<PrivateRoute><Create parentRoute={path} /></PrivateRoute>} />
        </Routes>
      </div>
    </AppContainer>
  );
};

export default EmployeeApp;

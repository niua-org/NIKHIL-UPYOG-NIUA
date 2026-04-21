import { AppContainer, PrivateRoute } from "@upyog/digit-ui-react-components";
import React from "react";
import { Route, Routes } from "react-router-dom";
import MapView from "../../components/MapView";
import ServiceTypes from "../../components/ServiceTypes";
import ViewOnMapAsset from "../../components/ViewOnMapAsset";

// EmployeeApp component defining routes for employee users
const EmployeeApp = ({ path }) => {
  return (
    <AppContainer>
      <Routes>
        <Route path={`${path}/servicetype`} element={<PrivateRoute><ServiceTypes /></PrivateRoute>} />
        <Route path={`${path}/mapview`} element={<PrivateRoute><MapView /></PrivateRoute>} />
        <Route path={`${path}/viewpolygon`} element={<PrivateRoute><ViewOnMapAsset /></PrivateRoute>} />
      </Routes>
    </AppContainer>
  );
};

export default EmployeeApp;

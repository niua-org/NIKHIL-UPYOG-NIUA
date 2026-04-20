import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { Navigate, Route, Routes, useLocation, useNavigate } from "react-router-dom";
import { TypeSelectCard, Loader } from "@upyog/digit-ui-react-components";
import { newConfig } from "../../../config/NewApplication/config";
import CheckPage from "./CheckPage";
import Response from "./Response";
import { useQueryClient } from "@tanstack/react-query";

const FileComplaint = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("FSM_CITIZEN_FILE_PROPERTY", {});
  const { data: commonFields, isLoading } = Digit.Hooks.fsm.useMDMS(stateId, "FSM", "CommonFieldsConfig");

  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("FSM_MUTATION_HAPPENED", false);
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("FSM_ERROR_DATA", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("FSM_MUTATION_SUCCESS_DATA", false);

  useEffect(() => {
    if (!pathname?.includes("new-application/response")) {
      setMutationHappened(false);
      clearSuccessData();
      clearError();
    }
  }, []);

  const configs = useMemo(() => {
    let config = [];
    commonFields?.forEach((obj) => {
      config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
    });
    const out = [...config];
    out.indexRoute = "select-trip-number";
    return out;
  }, [commonFields]);

  const match = Digit.Hooks.useWizardPath(configs);

  const goNext = (skipStep) => {
    const currentPath = pathname.split("/").pop();
    const { nextStep } = configs.find((routeObj) => routeObj.route === currentPath);
    let redirectWithHistory = (to, state) => navigate(to, state != null ? { state } : undefined);
    if (skipStep) {
      redirectWithHistory = (to, state) => navigate(to, state != null ? { replace: true, state } : { replace: true });
    }
    if (nextStep === null) {
      return redirectWithHistory(`${parentRoute}/new-application/check`);
    }
    redirectWithHistory(`${match.path}/${nextStep}`);
  };

  const submitComplaint = async () => {
    navigate(`${parentRoute}/new-application/response`);
  };

  function handleSelect(key, data, skipStep) {
    setParams({ ...params, ...{ [key]: { ...params[key], ...data } }, ...{ source: "ONLINE" } });
    goNext(skipStep);
  }

  const handleSkip = () => {};

  const handleSUccess = () => {
    clearParams();
    queryClient.invalidateQueries("FSM_CITIZEN_SEARCH");
    setMutationHappened(true);
  };

  if (isLoading) {
    return <Loader />;
  }

  return (
    <Routes>
      {configs.map((routeObj, index) => {
        const { component, texts, inputs, key } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route
            path={`${match.path}/${routeObj.route}`}
            key={index}
            element={<Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} />}
          />
        );
      })}
      <Route path={`${match.path}/check`} element={<CheckPage onSubmit={submitComplaint} value={params} />} />
      <Route path={`${match.path}/response`} element={<Response data={params} onSuccess={handleSUccess} />} />
      <Route path="*" element={<Navigate to={`${match.path}/${configs.indexRoute}`} replace />} />
    </Routes>
  );
};

export default FileComplaint;

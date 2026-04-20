import React, { useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Navigate, Route, Routes, useNavigate } from "react-router-dom";
import { newConfig as newConfigEDCR } from "../../../config/edcrConfig";
import { uuidv4 } from "../../../utils";
// import EDCRAcknowledgement from "./EDCRAcknowledgement";

const CreateEDCR = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("EDCR_CREATE", {});
  const [isShowToast, setIsShowToast] = useState(null);
  const [isSubmitBtnDisable, setIsSubmitBtnDisable] = useState(false);
  Digit.SessionStorage.set("EDCR_BACK", "IS_EDCR_BACK");

  const stateId = Digit.ULBService.getStateId();
  let { data: newConfig } = Digit.Hooks.obps.SearchMdmsTypes.getFormConfig(stateId, []);

  function handleSelect(key, data, skipStep, index) {
    setIsSubmitBtnDisable(true);
    const loggedInuserInfo = Digit.UserService.getUser();
    const userInfo = { id: loggedInuserInfo?.info?.uuid, tenantId: loggedInuserInfo?.info?.tenantId };
    let edcrRequest = {
      transactionNumber: "",
      edcrNumber: "",
      planFile: null,
      tenantId: "",
      RequestInfo: {
        apiId: "",
        ver: "",
        ts: "",
        action: "",
        did: "",
        authToken: "",
        key: "",
        msgId: "",
        correlationId: "",
        userInfo: userInfo
      }
    };

    const applicantName = data?.applicantName;
    const file = data?.file;
    const tenantId = data?.tenantId?.code;
    const transactionNumber = uuidv4();
    const appliactionType = "BUILDING_PLAN_SCRUTINY";
    const applicationSubType = "NEW_CONSTRUCTION";

    edcrRequest = { ...edcrRequest, tenantId };
    edcrRequest = { ...edcrRequest, transactionNumber };
    edcrRequest = { ...edcrRequest, applicantName };
    edcrRequest = { ...edcrRequest, appliactionType };
    edcrRequest = { ...edcrRequest, applicationSubType };

    let bodyFormData = new FormData();
    bodyFormData.append("edcrRequest", JSON.stringify(edcrRequest));
    bodyFormData.append("planFile", file);

    Digit.EDCRService.create({ data: bodyFormData }, tenantId)
      .then((result, err) => {
        setIsSubmitBtnDisable(false);
        if (result?.data?.edcrDetail) {
          setParams(result?.data?.edcrDetail);
          navigate(
            `/upyog-ui/citizen/obps/edcrscrutiny/apply/acknowledgement`, ///${result?.data?.edcrDetail?.[0]?.edcrNumber}
            { replace: true, state: { data: result?.data?.edcrDetail } }
          );
        }
      })
      .catch((e) => {
        setParams({data: e?.response?.data?.errorCode ? e?.response?.data?.errorCode : "BPA_INTERNAL_SERVER_ERROR", type: "ERROR"});
        setIsSubmitBtnDisable(false);
        setIsShowToast({ key: true, label: e?.response?.data?.errorCode ? e?.response?.data?.errorCode : "BPA_INTERNAL_SERVER_ERROR" })
      });

  }

  const handleSkip = () => { };
  const handleMultiple = () => { };

  const onSuccess = () => {
    sessionStorage.removeItem("CurrentFinancialYear");
    queryClient.invalidateQueries("TL_CREATE_TRADE");
  };
  const wizardConfig = useMemo(() => {
    let config = [];
    const mdms = newConfig?.EdcrConfig ? newConfig?.EdcrConfig : newConfigEDCR;
    mdms?.forEach((obj) => {
      config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
    });
    config.indexRoute = "home";
    return config;
  }, [newConfig]);

  const match = Digit.Hooks.useWizardPath(wizardConfig);

  const EDCRAcknowledgement = Digit?.ComponentRegistryService?.getComponent('EDCRAcknowledgement') ;

  return (
    <Routes>
      {wizardConfig.map((routeObj, index) => {
        const { component, texts, inputs, key } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route
            path={`${match.path}/${routeObj.route}`}
            key={index}
            element={
              <Component
                config={{ texts, inputs, key }}
                onSelect={handleSelect}
                onSkip={handleSkip}
                t={t}
                formData={params}
                onAdd={handleMultiple}
                isShowToast={isShowToast}
                isSubmitBtnDisable={isSubmitBtnDisable}
                setIsShowToast={setIsShowToast}
              />
            }
          />
        );
      })}
      <Route path={`${match.path}/acknowledgement`} element={<EDCRAcknowledgement data={params} onSuccess={onSuccess} />} />
      <Route path="*" element={<Navigate to={`${match.path}/${wizardConfig.indexRoute}`} replace />} />
    </Routes>
  );
};

export default CreateEDCR;

import { CitizenHomeCard, Loader, PTIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import InboxFilter from "./components/inbox/NewInboxFilter";
import ChallanGenerationCard from "./components/ChallanGenerationCard";
import EmployeeChallan from "./EmployeeChallan";
import AddressDetails from "./pageComponents/AddressDetails";
import ConsumerDetails from "./pageComponents/ConsumerDetails";
import ServiceDetails from "./pageComponents/ServiceDetails";
import EmployeeApp from "./pages/employee";
import NewChallan from "./pages/employee/NewChallan";
import SearchReceipt from "./pages/employee/SearchReceipt";
import SearchChallan from "./pages/employee/SearchChallan";
import ChallanStepperForm from "./pageComponents/ChallanStepper/ChallanStepperForm";
import OffenderDetails from "./pageComponents/OffenderDetails";
import OffenceDetails from "./pageComponents/OffenceDetails";
import ChallanSummary from "./pageComponents/ChallanSummary";
import ChallanDocuments from "./pageComponents/ChallanDocuments";
import getRootReducer from "../redux/reducer";
import ChallanResponseCitizen from "./components/ChallanResponseCitizen";
import DetailsCard from "./components/DetailsCard";   

/**
 * ChallanGenerationModule:
 * - Entry point for challan module (employee & citizen)
 * - Registers components and handles routing initialization
 */

export const ChallanGenerationModule = ({ stateCode, userType, tenants }) => {
  const moduleCode = "UC";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });
  Digit.SessionStorage.set("ChallanGeneration_TENANTS", tenants);
  if (isLoading) {
    return <Loader />;
  }
  const { path, url } = useRouteMatch();

  if (userType === "employee") {
    return <EmployeeApp path={path} url={url} userType={userType} />;
  } else return <CitizenApp />;
};

export const ChallanGenerationLinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PT_CREATE_PROPERTY112", {});

  useEffect(() => {
    clearParams();
  }, []);

  const links = [
    {
      link: `${matchPath}/search`,
      i18nKey: t("UC_SEARCH_AND_PAY"),
    },
    {
      link: `${matchPath}/My-Challans`,
      i18nKey: t("UC_MY_CHALLANS"),
    },
  ];

  return <CitizenHomeCard header={t("ACTION_TEST_MCOLLECT")} links={links} Icon={() => <PTIcon className="fill-path-primary-main" />} />;
};

export const ChallanReducers = getRootReducer;

const componentsToRegister = {
  ConsumerDetails,
  ServiceDetails,
  AddressDetails,
  ChallanGenerationCard,
  ChallanGenerationModule,
  ChallanGenerationLinks,
  MCollectEmployeeChallan: EmployeeChallan,
  MCollectNewChallan: NewChallan,
  SearchReceipt,
  SearchChallan,
  MCOLLECT_INBOX_FILTER: (props) => <InboxFilter {...props} />,
  ChallanStepperForm,
  OffenderDetails,
  OffenceDetails,
  ChallanSummary,
  ChallanDocuments,
  ChallanResponseCitizen
};

export const initChallanGenerationComponents = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};

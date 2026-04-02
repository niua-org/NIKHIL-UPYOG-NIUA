// Importing UI components from Digit UI library
import { Banner, Card, CardText, LinkButton, ActionBar, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";

// React and hooks
import React, { useState, useEffect } from "react";

// Router hooks for navigation and URL handling
import { useLocation, Link, useParams } from "react-router-dom";

// i18n for translations
import { useTranslation } from "react-i18next";

// Utility to extract query params from URL
import * as func from "./Utils/getQueryParams";

// Utility to download & print challan
import { downloadAndPrintChallan } from "../../utils";

/**
 * MCollectAcknowledgement Component
 * ------------------------------------------------------
 * Purpose:
 * - Displays acknowledgement screen after challan actions
 *   (Generate / Update / Cancel)
 * - Shows success/cancel banner
 * - Provides option to:
 *    → Print challan
 *    → Go to home
 *    → Proceed to payment (if not cancelled)
 */
const MCollectAcknowledgement = () => {

  // Access current URL location
  const location = useLocation();

  // State to store query params (challanNumber, status, tenantId, etc.)
  const [params, setParams] = useState({});

  // Hook to check if form was in edit mode
  const { isEdit } = Digit.Hooks.useQueryParams();

  /**
   * Extract query parameters whenever URL changes
   * Example:
   * ?challanNumber=123&applicationStatus=CANCELLED
   */
  useEffect(() => {
    setParams(func.getQueryStringParams(location.search));
  }, [location]);

  // Translation function
  const { t } = useTranslation();

  /**
   * Handles challan printing
   * Calls utility function with challan number
   */
  const printReciept = async () => {
    const challanNo = params?.challanNumber;
    downloadAndPrintChallan(challanNo, "print");
  };

  return (
    <div>

      {/* ================= CANCELLED CASE ================= */}
      {params?.applicationStatus === "CANCELLED" ? (
        <Card>

          {/* Success banner for cancellation */}
          <Banner
            message={t("UC_BILL_CANCELLED_SUCCESS_MESSAGE")}
            applicationNumber={params?.challanNumber}
            info={t("UC_CHALLAN_NO")}
            successful={true}
          />

          {/* Sub message */}
          <CardText>{t("UC_BILL_CANCELLED_SUCCESS_SUB_MESSAGE")}</CardText>

          {/* Print challan button */}
          {"generatePdfKey" ? (
            <div
              className="primary-label-btn d-grid"
              style={{ marginLeft: "unset" }}
              onClick={printReciept}
            >
              {/* Print Icon */}
              <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
                <path d="M0 0h24v24H0z" fill="none" />
                <path d="M19 8H5c-1.66 0-3 1.34-3 3v6h4v4h12v-4h4v-6c0-1.66-1.34-3-3-3zm-3 11H8v-5h8v5zm3-7c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm-1-9H6v4h12V3z" />
              </svg>

              {/* Button label */}
              {t("UC_PRINT_CHALLAN_LABEL")}
            </div>
          ) : null}

          {/* Action buttons */}
          <ActionBar className="challan-emp-acknowledgement">

            {/* Redirect to home */}
            <Link to={`/upyog-ui/employee`} style={{ marginRight: "1rem" }}>
              <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
            </Link>

          </ActionBar>
        </Card>
      ) : (

        /* ================= SUCCESS / GENERATED CASE ================= */
        <Card>

          {/* Banner for generated or updated challan */}
          <Banner
            message={
              !isEdit
                ? t("UC_BILL_GENERATED_SUCCESS_MESSAGE")
                : t("UC_BILL_UPDATED_SUCCESS_MESSAGE")
            }
            applicationNumber={params?.challanNumber}
            info={t("UC_CHALLAN_NO")}
            successful={true}
          />

          {/* Sub message */}
          <CardText>{t("UC_BILL_GENERATION_MESSAGE_SUB")}</CardText>

          {/* Print challan button */}
          {"generatePdfKey" ? (
            <div
              className="primary-label-btn d-grid"
              style={{ marginLeft: "unset" }}
              onClick={printReciept}
            >
              {/* Print icon */}
              <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
                <path d="M0 0h24v24H0z" fill="none" />
                <path d="M19 8H5c-1.66 0-3 1.34-3 3v6h4v4h12v-4h4v-6c0-1.66-1.34-3-3-3zm-3 11H8v-5h8v5zm3-7c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm-1-9H6v4h12V3z" />
              </svg>

              {t("UC_PRINT_CHALLAN_LABEL")}
            </div>
          ) : null}

          {/* Action buttons */}
          <ActionBar className="challan-emp-acknowledgement">

            {/* Go to home */}
            <Link to={`/upyog-ui/employee`} style={{ marginRight: "1rem" }}>
              <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
            </Link>

            {/* Navigate to payment collection screen */}
            <Link
              to={{
                pathname: `/upyog-ui/employee/payment/collect/${params?.serviceCategory}/${params?.challanNumber}/tenantId=${params?.tenantId}?workflow=mcollect`,
              }}
            >
              <SubmitBar label={t("UC_BUTTON_PAY")} />
            </Link>

          </ActionBar>
        </Card>
      )}
    </div>
  );
};

export default MCollectAcknowledgement;
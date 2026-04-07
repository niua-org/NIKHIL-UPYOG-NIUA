import React from "react";

/**
 * Configuration for cancel challan action:
 * - Defines modal labels
 * - Provides form structure for rejection comment
 */

export const configMCollectRejectApplication = ({ t, action }) => {
  return {
    label: {
      heading: `CANCEL_CHALLAN_HEADER`,
      submit: `CANCEL_YES`,
      cancel: "CANCEL_NO",
    },
    form: [
      {
        body: [
          {
            label: t("CANCEL_COMMENT_LABEL"),
            type: "textarea",
            populators: {
              name: "comments",
            },
          }
        ],
      },
    ],
  };
};

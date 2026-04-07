import { Card, PTIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

/**
 * InboxLinks component:
 * - Displays navigation links inside a card
 * - Supports internal routing and external hyperlinks
 * - Uses translation for text labels
 */

const InboxLinks = ({ parentRoute, businessService }) => {
  const { t } = useTranslation();
  const [links, setLinks] = useState([]);
  const allLinks = [
    {
      // text: t("UC_GENERATE_NEW_CHALLAN"),
      // link: "/upyog-ui/employee/challangeneration/generate-challan",
      // roles: [],
    },
    // {
    //   text: "Search Receipt",
    //   link: "/upyog-ui/employee/mcollect/search-receipt",
    //   roles: [],
    // },
    // {
    //   text: "Search Challan",
    //   link: "/upyog-ui/employee/mcollect/search-challan",
    //   roles: [],
    // },
    // {
    //   text: "Search and Pay",
    //   link: "/upyog-ui/employee/mcollect/search-bill",
    //   roles: [],
    // },
    // {
    //   text: "Group Bill",
    //   link: "/upyog-ui/employee/mcollect/group-bill",
    //   roles: [],
    // },
  ];

  useEffect(() => {
    let linksToShow = allLinks;
    setLinks(linksToShow);
  }, []);

  const GetLogo = () => (
    <div className="header" style={{ justifyContent: "flex-start" }}>
      <span className="logo">
        <PTIcon />
      </span>{" "}
      <span className="text">
        {t("ACTION_TEST_CHALLANGENERATION")}
      </span>
    </div>
  );

  return (
    <Card className="employeeCard filter inboxLinks">
      <div
       
        className="complaint-links-container"
      >
        {GetLogo()}
        <div className="body">
          {links.map(({ link, text, hyperlink = false, accessTo = [] }, index) => {
            return (
              <span className="link" key={index}>
                {hyperlink ? <a href={link}>{t(text)}</a> : <Link to={link}>{t(text)}</Link>}
              </span>
            );
          })}
        </div>
      </div>
    </Card>
  );
};

export default InboxLinks;

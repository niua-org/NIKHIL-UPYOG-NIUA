// Import React and required libraries
import React from "react";
import PropTypes from "prop-types";
import { Link } from "react-router-dom";

/**
 * Details Component
 * --------------------------------------
 * Reusable component to display a label-value pair
 * Example:
 *  Label → "Name"
 *  Value → "John Doe"
 */
const Details = ({ label, name, onClick }) => {
  return (
    <div className="detail" onClick={onClick}>
      {/* Label Section */}
      <span className="label">
        <h2>{label}</h2>
      </span>

      {/* Value Section */}
      <span className="name" style={{ overflowWrap: "break-word" }}>
        {name}
      </span>
    </div>
  );
};

/**
 * DetailsCard Component
 * --------------------------------------
 * Purpose:
 * - Displays a list of data objects as cards
 * - Supports:
 *    → Navigation (via Link)
 *    → Selection (highlight selected items)
 *    → Click handling
 *    → Dynamic rendering of fields
 */
const DetailsCard = ({
  data,
  serviceRequestIdKey,
  linkPrefix,
  handleSelect,
  selectedItems,
  keyForSelected,
  handleDetailCardClick,
  isTwoDynamicPrefix = false,
  getRedirectionLink,
  handleClickEnabled = true,
}) => {

  // Get current tenantId (used in routing)
  const tenantId = Digit.ULBService.getCurrentTenantId();

  /**
   * CASE 1: When linkPrefix & serviceRequestIdKey are provided
   * → Each card becomes clickable and redirects using <Link>
   */
  if (linkPrefix && serviceRequestIdKey) {
    return (
      <div>
        {data?.map((object, itemIndex) => {

          // Debug log (can be removed in production)
          console.log("object==", object);

          return (
            <Link
              key={itemIndex}
              to={`${linkPrefix}${object?.["Challan No"]?.props?.children}/${tenantId}`}
            >
              <div className="details-container">

                {/* Dynamically render all key-value pairs */}
                {Object.keys(object).map((name, index) => {

                  // Skip specific fields
                  if (name === "applicationNo" || name === "Vehicle Log") return null;

                  return <Details label={name} name={object[name]} key={index} />;
                })}

              </div>
            </Link>
          );
        })}
      </div>
    );
  }

  /**
   * CASE 2: Normal card rendering (no navigation)
   * → Supports selection & click handling
   */
  return (
    <div>
      {data.map((object, itemIndex) => {
        return (
          <div
            key={itemIndex}

            // Highlight selected card with red border
            style={{
              border: selectedItems?.includes(object[keyForSelected])
                ? "2px solid #a82227"
                : "2px solid #fff",
            }}

            className="details-container"

            // Handle card selection click
            onClick={() => handleClickEnabled && handleSelect(object)}
          >
            {Object.keys(object)

              // Filter out hidden fields
              .filter(
                (rowEle) =>
                  !(
                    typeof object[rowEle] == "object" &&
                    object[rowEle]?.hidden == true
                  )
              )

              // Render each field
              .map((name, index) => {
                return (
                  <Details
                    label={name}
                    name={object[name]}
                    key={index}

                    // Handle inner detail click
                    onClick={() =>
                      handleClickEnabled && handleDetailCardClick(object)
                    }
                  />
                );
              })}
          </div>
        );
      })}
    </div>
  );
};

/**
 * PropTypes validation
 * Ensures 'data' is always an array
 */
DetailsCard.propTypes = {
  data: PropTypes.array,
};

/**
 * Default props
 * Prevents undefined errors if data is not passed
 */
DetailsCard.defaultProps = {
  data: [],
};

// Export component
export default DetailsCard;
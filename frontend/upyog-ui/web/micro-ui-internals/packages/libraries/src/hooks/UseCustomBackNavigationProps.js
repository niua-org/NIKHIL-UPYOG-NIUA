import { useEffect } from 'react';
import { useNavigate } from "react-router-dom";

/**
 * Custom hook to handle back button navigation
 * @author Shivank - NIUA
 * 
 * @param {Object} config - Configuration object
 * @param {string} config.redirectPath - Path to redirect to when back button is clicked
 * @param {boolean} [config.enableConfirmation=false] - Whether to show confirmation dialog
 * @param {string} [config.confirmationMessage='Are you sure you want to leave this page?'] - Custom confirmation message
 */

export const useCustomBackNavigation = ({
  redirectPath,
  enableConfirmation = false,
  confirmationMessage = "Are you sure you want to leave this page?",
}) => {
  const navigate = useNavigate();

  useEffect(() => {
    // Push dummy state to block browser back
    window.history.pushState(null, "", window.location.pathname);

    const handleBackButton = (event) => {
      event.preventDefault();

      if (enableConfirmation) {
        const shouldRedirect = window.confirm(confirmationMessage);

        if (shouldRedirect) {
          navigate(redirectPath);
        } else {
          window.history.pushState(null, "", window.location.pathname);
        }
      } else {
        navigate(redirectPath);
      }
    };

    window.addEventListener("popstate", handleBackButton);

    return () => {
      window.removeEventListener("popstate", handleBackButton);
    };
  }, [navigate, redirectPath, enableConfirmation, confirmationMessage]);
};
import React, { useState, useEffect } from "react";
import MCollectActionModal from "./MCollectActionModal";

/**
 * ActionModal component:
 * - Wrapper for MCollectActionModal
 * - Passes all props directly
 */

const ActionModal = (props) => {
  return <MCollectActionModal {...props} />;
};

export default ActionModal;

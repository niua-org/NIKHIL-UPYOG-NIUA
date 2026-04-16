import { MdmsService } from "../../services/elements/MDMS";
import { queryTemplate } from "../../common/queryTemplate";

const useSVDoc = (tenantId, moduleCode, type, config = {}) => {
  const useSVDocumentsRequiredScreen = () => {
    return queryTemplate({ queryKey: ["SV_DOCUMENT_REQ_SCREEN"], queryFn: () => MdmsService.getSVDocuments(tenantId, moduleCode), config });
  };

  const _default = () => {
    return queryTemplate({ queryKey: [tenantId, moduleCode, type], queryFn: () => MdmsService.getMultipleTypes(tenantId, moduleCode, type), config });
  };

  switch (type) {
    case "Documents":
      return useSVDocumentsRequiredScreen();
    default:
      return _default();
  }
};

export default useSVDoc;

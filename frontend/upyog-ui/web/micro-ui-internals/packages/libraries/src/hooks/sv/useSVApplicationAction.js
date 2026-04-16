import { mutationTemplate } from "../../common/mutationTemplate";
import ApplicationUpdateActionsSV from "../../services/molecules/SV/ApplicationUpdateActionsSV";

/** The following function is used for the mutation function */

const useSVApplicationAction = (tenantId) => {
  const mutationFn = (applicationData) => ApplicationUpdateActionsSV(applicationData, tenantId);
  return mutationTemplate({ mutationFn });
};

export default useSVApplicationAction;

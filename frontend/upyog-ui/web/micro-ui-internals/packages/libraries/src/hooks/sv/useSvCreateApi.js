import { mutationTemplate } from "../../common/mutationTemplate";
import { SVService } from "../../services/elements/SV";

/**
 * Custom hook for create API for street vending.
 * If type is true, calls SVService.create; otherwise SVService.update.
 */

export const useSvCreateApi = (tenantId, type = true) => {
  if (type) {
    return mutationTemplate({ mutationFn: (data) => SVService.create(data, tenantId) });
  } else {
    return mutationTemplate({ mutationFn: (data) => SVService.update(data, tenantId) });
  }
};

export default useSvCreateApi;

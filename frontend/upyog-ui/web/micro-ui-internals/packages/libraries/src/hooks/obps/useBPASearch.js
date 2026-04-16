import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "@tanstack/react-query";
import { OBPSService } from "../../services/elements/OBPS";

const useBPASearch = (tenantId, filters = {}, config = {}) => {
  const client = useQueryClient();

  const queryKey = [
    "OBPS_BPA_SEARCH",
    tenantId,
    JSON.stringify(filters),
  ];

  const query = queryTemplate({
    queryKey,
    queryFn: async () => {
      const response = await OBPSService.BPASearch(tenantId, filters);
      return response?.BPA || [];
    },
    config,
  });

  return {
    ...query,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useBPASearch;
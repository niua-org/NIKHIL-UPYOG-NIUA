import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "@tanstack/react-query";

const combineResponses = (complaintDetailsResponse, workflowInstances) => {
  const wfMap = workflowInstances.ProcessInstances.reduce(
    (acc, item) => ({ ...acc, [item.businessId]: item }),
    {}
  );

  return complaintDetailsResponse.ServiceWrappers.map(
    (complaint) => {
      const wf =
        wfMap[complaint.service.serviceRequestId];
      if (!wf) return null;

      return {
        serviceRequestId:
          complaint.service.serviceRequestId,
        complaintSubType: complaint.service.serviceCode,
        priorityLevel: complaint.service.priority,
        locality: complaint.service.address.locality.code,
        status: complaint.service.applicationStatus,
        taskOwner: wf.assignes?.[0]?.name || "-",
        sla: Math.round(
          wf.businesssServiceSla /
            (24 * 60 * 60 * 1000)
        ),
        tenantId: complaint.service.tenantId,
      };
    }
  ).filter(Boolean);
};

const useInboxData = (searchParams) => {
  const client = useQueryClient();

  const queryKey = [
    "PGR_INBOX",
    JSON.stringify(searchParams),
  ];

  const query = queryTemplate({
    queryKey,
    queryFn: async () => {
      const tenantId =
        Digit.ULBService.getCurrentTenantId();

      const { limit, offset } = searchParams;

      const appFilters = {
        ...searchParams.filters.pgrQuery,
        ...searchParams.search,
        limit,
        offset,
      };

      const complaintRes =
        await Digit.PGRService.search(
          tenantId,
          appFilters
        );

      const serviceIds = complaintRes.ServiceWrappers.map(
        (s) => s.service.serviceRequestId
      ).join();

      const workflow =
        await Digit.WorkflowService.getByBusinessId(
          tenantId,
          serviceIds
        );

      return combineResponses(
        complaintRes,
        workflow
      );
    },
    config: { staleTime: Infinity },
  });

  return {
    ...query,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useInboxData;
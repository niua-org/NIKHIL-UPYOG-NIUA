import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "@tanstack/react-query";

const getThumbnails = async (ids, tenantId) => {
  const res = await Digit.UploadServices.Filefetch(ids, tenantId);
  if (res.data.fileStoreIds?.length) {
    return {
      thumbs: res.data.fileStoreIds.map((o) => o.url.split(",")[3]),
      images: res.data.fileStoreIds.map((o) =>
        Digit.Utils.getFileUrl(o.url)
      ),
    };
  }
  return null;
};

const fetchComplaintDetails = async (tenantId, id) => {
  const serviceDefs = await Digit.MDMSService.getServiceDefs(
    tenantId,
    "PGR"
  );

  const { service, workflow } =
    (await Digit.PGRService.search(tenantId, {
      serviceRequestId: id,
    })).ServiceWrappers[0] || {};

  if (!service || !workflow || !serviceDefs) return {};

  const complaintType = serviceDefs.find(
    (d) => d.serviceCode === service.serviceCode
  )?.menuPath?.toUpperCase();

  const ids =
    workflow.verificationDocuments
      ?.filter((d) => d.documentType === "PHOTO")
      ?.map((p) => p.fileStoreId || p.id) || [];

  const thumbnails = ids.length
    ? await getThumbnails(ids, service.tenantId)
    : null;

  return {
    service,
    workflow,
    thumbnails,
    complaintType,
  };
};

const useComplaintDetails = ({ tenantId, id }) => {
  const client = useQueryClient();

  const queryKey = ["PGR_COMPLAINT_DETAILS", tenantId, id];

  const query = queryTemplate({
    queryKey,
    queryFn: () => fetchComplaintDetails(tenantId, id),
  });

  return {
    ...query,
    complaintDetails: query.data,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useComplaintDetails;
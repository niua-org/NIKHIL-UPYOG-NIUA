/**
 * Following hook is used to get data of applications from backend in Street Vending module and returns the data in an object "SVDetail"
 */

import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

const useSvSearchApplication = ({ tenantId, filters, auth, searchedFrom = "" }, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    if (data.SVDetail.length > 0) data.SVDetail[0].applicationNo = data.SVDetail[0].applicationNo || [];
    return data;
  };

  const { isLoading, error, data, isSuccess } = queryTemplate({ queryKey: ["streetVendingSearchList", tenantId, filters, auth, config], queryFn: () => Digit.SVService.search(args), select: defaultSelect, config });

  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries({ queryKey: ["streetVendingSearchList", tenantId, filters, auth] }) };
};

export default useSvSearchApplication;

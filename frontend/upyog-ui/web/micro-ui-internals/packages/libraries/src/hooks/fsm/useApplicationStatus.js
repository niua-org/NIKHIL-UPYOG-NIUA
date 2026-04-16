import { useTranslation } from "react-i18next";
import { queryTemplate } from "../../common/queryTemplate";

const useApplicationStatus = (select, isEnabled = true, statusMap = []) => {
  const { t } = useTranslation();

  const userInfo = Digit.UserService.getUser();
  const userRoles = userInfo.info.roles.map((r) => r.code);

  const tenantId = Digit.ULBService.getCurrentTenantId();

  const queryKey = ["FSM_APPLICATION_STATUS", isEnabled];

  const getStates = (businessServices) => {
    let states = [];
    businessServices.forEach((data) => {
      states = states.concat(data.states);
    });
    return states;
  };

  const fetchFn = async () => {
    const WorkflowService = await Digit.WorkflowService.init(
      tenantId,
      "FSM,FSM_POST_PAY_SERVICE,PAY_LATER_SERVICE,FSM_ADVANCE_PAY_SERVICE,FSM_ADVANCE_PAY_SERVICE_V1,FSM_ZERO_PAY_SERVICE"
    );

    return getStates(WorkflowService.BusinessServices);
  };

  const roleWiseSelect = (data) =>
    data
      .filter((state) => state.applicationStatus)
      .filter((status) => {
        if (!status.actions) return false;
        const roles = status.actions.flatMap((a) => a.roles);
        return roles.some((r) => userRoles.includes(r));
      })
      .map((state) => ({
        name: t(`CS_COMMON_FSM_${state.applicationStatus}`),
        code: state.applicationStatus,
        id:
          statusMap?.find((e) => e.applicationstatus === state.applicationStatus)?.statusid ||
          state.uuid,
      }));

  const defaultSelect = (data) =>
    data
      .filter((state) => state.applicationStatus)
      .map((state) => ({
        name: t(`CS_COMMON_FSM_${state.applicationStatus}`),
        code: state.applicationStatus,
        id:
          statusMap?.find((e) => e.applicationstatus === state.applicationStatus)?.statusid ||
          state.uuid,
      }));

  return queryTemplate({
    queryKey,
    queryFn: fetchFn,
    enabled: isEnabled,
    select: select ? roleWiseSelect : defaultSelect,
  });
};

export default useApplicationStatus;
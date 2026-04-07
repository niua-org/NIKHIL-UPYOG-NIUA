import React, { useEffect, useState } from "react";
import { TextInput, CardLabel, Dropdown, MobileNumber, TextArea, ActionBar, SubmitBar } from "@upyog/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { Loader } from "../components/Loader";

/**
 * OffenderDetails component:
 * - Captures offender details (name, mobile, address)
 * - Uses react-hook-form for validation and submission
 */

const OffenderDetails = ({ t, goNext, currentStepData, onGoBack }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const user = Digit.UserService.getUser();
  const [loader, setLoader] = useState(false);

  const {
    control,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm({
    defaultValues: {
      name: user?.info?.name || "",
      emailId: user?.info?.emailId || "",
      mobileNumber: user?.info?.mobileNumber || "",
      address: "",
    },
  });

  const onSubmit = async (data) => {
    goNext(data);
  };

  useEffect(() => {
    console.log("currentStepData", currentStepData);
  }, [currentStepData, setValue]);

  return (
    <React.Fragment>
      <form onSubmit={handleSubmit(onSubmit)}>
        <div style={{ width: "50%" }}>

          <div className="challan-bottom-style"  >
            <CardLabel>
              {`${t("NOC_APPLICANT_MOBILE_NO_LABEL")}`} <span className="requiredField">*</span>
            </CardLabel>
            <Controller
              control={control}
              name="mobileNumber"
              rules={{
                required: "Mobile number is required",
                pattern: {
                  value: /^[6-9]\d{9}$/,
                  message: "Enter a valid 10-digit mobile number",
                },
              }}
              render={(props) => (
                <MobileNumber
                  
                  value={props.value}
                  onChange={props.onChange} // ✅ don't wrap it
                  onBlur={props.onBlur}
                  t={t}
                />
              )}
            />
            {errors?.mobileNumber && <p className="requiredField">{errors.mobileNumber.message}</p>}
          </div>

          <div className="challan-bottom-style"  >
            <CardLabel>
              {`${t("BPA_BASIC_DETAILS_APPLICATION_NAME_LABEL")}`} <span className="requiredField">*</span>
            </CardLabel>
            <Controller
              control={control}
              name="name"
              rules={{
                required: "Name is required",
                minLength: { value: 2, message: "Name must be at least 2 characters" },
              }}
              render={(props) => (
                <TextInput
                  
                  value={props.value}
                  error={errors?.name?.message}
                  onChange={(e) => {
                    props.onChange(e.target.value);
                  }}
                  onBlur={(e) => {
                    props.onBlur(e);
                  }}
                  t={t}
                />
              )}
            />
            {errors?.name && <p className="requiredField">{errors.name.message}</p>}
          </div>

          <div className="challan-bottom-style"  >
            <CardLabel>
              {`${t("PT_COMMON_COL_ADDRESS")}`} <span className="requiredField">*</span>
            </CardLabel>
            <Controller
              control={control}
              name="address"
              rules={{
                required: "Address is required",
                minLength: { value: 5, message: "Address must be at least 5 characters" },
              }}
              render={(props) => (
                <TextArea
                  
                  name="address"
                  value={props.value}
                  onChange={(e) => {
                    props.onChange(e.target.value);
                  }}
                  onBlur={(e) => {
                    props.onBlur(e);
                  }}
                  t={t}
                />
              )}
            />
            {errors?.address && <p className="requiredField">{errors.address.message}</p>}
          </div>
        </div>

        <ActionBar>
          <SubmitBar className="submit-bar-back" label="Back" onSubmit={onGoBack} />
          <SubmitBar label="Next" submit="submit" />
        </ActionBar>
        {/* <button type="submit">submit</button> */}
      </form>
      {loader && <Loader page={true} />}
    </React.Fragment>
  );
};

export default OffenderDetails;

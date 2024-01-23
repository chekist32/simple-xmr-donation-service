import { useState } from "react";

import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod/dist/zod";

import "./UserSettingsSecurityView.css";
import "../GlobalUserSettingsSubmoduleStyles.css";

import CustomInput from "@shared-components/custom_input/CustomInput";
import CustomButton from "@shared-components/custom_button/CustomButton";
import ErrorMessage from "@components/error_message/ErrorMessage";
import axios from "axios";
import CeneteredPopupAlert from "@components/centered_popup_alert/CeneteredPopupAlertComponent";

function UserSettingsSecurityView() {
  const [errorMsg, setErrorMsg] = useState("");
  const [errorObj, setErrorObj] = useState({});

  const passwordValidationSchema = z
    .object({
      newPassword: z.string().min(1, "Password is required"),
      repeatNewPassword: z.string().min(1, "Password is required"),
    })
    .refine((data) => data.newPassword === data.repeatNewPassword, {
      message: "Password must match",
      path: ["repeatNewPassword"],
    });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(passwordValidationSchema),
  });

  const onSubmitPasswordHandler = async (data) => {
    try {
      setErrorMsg("");
      setErrorObj({});
      await axios.post(
        import.meta.env.VITE_API_BASE_URL + "/api/auth/changePassword",
        data,
        { withCredentials: true },
      );
      reset();
      setShowAlert(true);
    } catch (err) {
      const errorRes = err.response;

      if (typeof errorRes?.data?.message === "string")
        setErrorMsg(errorRes.data.message);
      else if (typeof errorRes?.data?.message === "object")
        setErrorObj(errorRes.data.message);
    }
  };

  const [showAlert, setShowAlert] = useState(false);

  return (
    <>
      <div className="default-user-settings-submodule-view-styles-notcentered  user-settings-security-view">
        <div className="default-user-settings-submodule-view-header-styles user-settings-security-view-header">
          Security
        </div>
        <div className="default-user-settings-submodule-view-main-styles user-settings-security-view-main">
          <div className="user-settings-security-view-main-password">
            <form
              className="user-settings-security-view-password-form"
              onSubmit={handleSubmit(onSubmitPasswordHandler)}
            >
              <label>New password:</label>
              <CustomInput
                inputProps={{
                  ...register("newPassword"),
                  placeholder: "New password",
                  maxLength: "254",
                }}
              />
              {errors?.newPassword && (
                <ErrorMessage errorMsg={errors.newPassword.message} />
              )}
              {errorObj?.newPassword && (
                <ErrorMessage errorMsg={errorObj.newPassword} />
              )}

              <label>Repeat password:</label>
              <CustomInput
                inputProps={{
                  ...register("repeatNewPassword"),
                  placeholder: "Repeat password",
                  maxLength: "254",
                }}
              />
              {errors?.repeatNewPassword && (
                <ErrorMessage errorMsg={errors.repeatNewPassword.message} />
              )}
              {errorObj?.repeatNewPassword && (
                <ErrorMessage errorMsg={errorObj.repeatNewPassword} />
              )}

              <CustomButton buttonProps={{ type: "submit", width: "11rem" }}>
                Change password
              </CustomButton>
              {showAlert && (
                <CeneteredPopupAlert
                  mainContent="Password successfully has been changed"
                  timeout={0}
                  closeHandler={() => setShowAlert(false)}
                />
              )}
            </form>
          </div>
        </div>
      </div>
    </>
  );
}

export default UserSettingsSecurityView;

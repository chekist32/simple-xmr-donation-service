import { useState } from "react";

import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod/dist/zod";

import "./UserSettingsAccountView.css";
import "../GlobalUserSettingsSubmoduleStyles.css";
import CustomInput from "@shared-components/custom_input/CustomInput";
import CustomButton from "@shared-components/custom_button/CustomButton";
import ErrorMessage from "@components/error_message/ErrorMessage";
import axios from "axios";
import CeneteredPopupAlert from "@components/centered_popup_alert/CeneteredPopupAlertComponent";

function UserSettingsAccountView() {
  const [errorMsg, setErrorMsg] = useState("");
  const [errorObj, setErrorObj] = useState({});
  const [showAlert, setShowAlert] = useState(false);

  const emailValidationSchema = z
    .object({
      newEmail: z
        .string()
        .min(1, "Email is required")
        .email("Bad email (example@email.com)"),
      repeatNewEmail: z
        .string()
        .min(1, "Email is required")
        .email("Bad email (example@email.com)"),
    })
    .refine((data) => data.newEmail === data.repeatNewEmail, {
      message: "Email must match",
      path: ["repeatNewEmail"],
    });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(emailValidationSchema),
  });

  const onSubmitEmailHandler = async (data) => {
    try {
      setErrorMsg("");
      setErrorObj({});
      await axios.post(
        import.meta.env.VITE_API_BASE_URL + "/api/auth/changeEmail",
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

  return (
    <div className="default-user-settings-submodule-view-styles-notcentered  user-settings-account-view">
      <div className="default-user-settings-submodule-view-header-styles user-settings-account-view-header">
        Account
      </div>
      <div className="default-user-settings-submodule-view-main-styles user-settings-account-view-main">
        <div className="user-settings-account-view-main-email">
          <form
            className="user-settings-account-view-email-form"
            onSubmit={handleSubmit(onSubmitEmailHandler)}
          >
            <label>New email:</label>
            <CustomInput
              inputProps={{
                ...register("newEmail"),
                placeholder: "New email",
                maxLength: "254",
              }}
            />
            {errors?.newEmail && (
              <ErrorMessage errorMsg={errors.newEmail.message} />
            )}
            {errorObj?.newEmail && (
              <ErrorMessage errorMsg={errorObj.newEmail} />
            )}

            <label>Repeat email:</label>
            <CustomInput
              inputProps={{
                ...register("repeatNewEmail"),
                placeholder: "Repeat email",
                maxLength: "254",
              }}
            />
            {errors?.repeatNewEmail && (
              <ErrorMessage errorMsg={errors.repeatNewEmail.message} />
            )}
            {errorObj?.repeatNewEmail && (
              <ErrorMessage errorMsg={errorObj.repeatNewEmail} />
            )}

            <CustomButton buttonProps={{ type: "submit", width: "9rem" }}>
              Change email
            </CustomButton>
            {showAlert && (
              <CeneteredPopupAlert
                mainContent="Confirmation email has been sent"
                timeout={0}
                closeHandler={() => setShowAlert(false)}
              />
            )}
          </form>
        </div>
      </div>
    </div>
  );
}

export default UserSettingsAccountView;

import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate, useSearchParams } from "react-router-dom";

import { zodResolver } from "@hookform/resolvers/zod/dist/zod";
import { z } from "zod";
import axios from "axios";

import "./ResetPasswordView.css";
import CustomInput from "../../components/custom_input/CustomInput";
import CustomButton from "../../components/custom_button/CustomButton";
import LoadingSpinner from "../../components/loading_spinner/LoadingSpinner";
import CeneteredPopupAlert from "../../components/centered_popup_alert/CeneteredPopupAlertComponent";

function ResetPasswordView() {
  const [errorMsg, setErrorMsg] = useState("");
  const [errorObj, setErrorObj] = useState("");
  const [token, setToken] = useState("");
  const [searchParams] = useSearchParams();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showPopupWithToken, setShowPopupWithToken] = useState(false);
  const [showPopupWithoutToken, setShowPopupWithoutToken] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    setToken(searchParams.get("token"));
  }, []);

  const validationSchemaWithToken = z
    .object({
      newPassword: z.string().min(1, "Password is required"),
      repeatNewPassword: z.string().min(1, "Password is required"),
    })
    .refine((data) => data.newPassword === data.repeatNewPassword, {
      message: "Password must match",
      path: ["repeatNewPassword"],
    });

  const withTokenForm = useForm({
    resolver: zodResolver(validationSchemaWithToken),
  });

  const onSubmitHandlerWithToken = async (data) => {
    try {
      setErrorMsg("");
      setIsSubmitting(true);
      await axios.put(
        import.meta.env.VITE_API_BASE_URL + "/api/auth/resetPassword",
        data,
        { withCredentials: true, params: { token: token } },
      );
      setShowPopupWithToken(true);
      setTimeout(() => navigate("/signin", { replace: true }), 2500);
    } catch (err) {
      const errorRes = err.response;

      if (errorRes?.data?.message) {
        setErrorMsg(errorRes.data.message);
      }
    }
    setIsSubmitting(false);
    withTokenForm.reset();
  };

  function resetPasswordWithToken() {
    return (
      <>
        <div className="view resetPassword-view">
          <div className="resetPassword-container">
            <div className="title">Reset password</div>
            <form
              className="resetPassword-form"
              onSubmit={withTokenForm.handleSubmit(onSubmitHandlerWithToken)}
            >
              <CustomInput
                inputProps={{
                  ...withTokenForm.register("newPassword"),
                  type: "password",
                  placeholder: "Password",
                  maxLength: "64",
                }}
              />
              <CustomInput
                inputProps={{
                  ...withTokenForm.register("repeatNewPassword"),
                  type: "password",
                  placeholder: "Repeat password",
                  maxLength: "64",
                }}
              />
              {errorMsg && (
                <div className="error-message-container">
                  <span className="error-message">{errorMsg}</span>
                </div>
              )}
              <CustomButton buttonProps={{ type: "submit" }}>
                Reset Password
              </CustomButton>
            </form>
          </div>
        </div>
      </>
    );
  }

  const validationSchemaWithoutToken = z.object({
    email: z
      .string()
      .min(1, "Email is required")
      .email("Bad email (example@email.com)"),
  });

  const onSubmitHandlerWithoutToken = async (data) => {
    try {
      setErrorMsg("");
      setIsSubmitting(true);
      await axios.post(
        import.meta.env.VITE_API_BASE_URL + "/api/auth/resetPassword",
        data,
        { withCredentials: true },
      );
      setShowPopupWithoutToken(true);
    } catch (err) {
      const errorRes = err.response;

      if (errorRes?.data?.message) {
        setErrorMsg(errorRes.data.message);
      }
    }
    setIsSubmitting(false);
    withoutTokenForm.reset();
  };

  const withoutTokenForm = useForm({
    resolver: zodResolver(validationSchemaWithoutToken),
  });

  function resetPasswordWithoutToken() {
    return (
      <>
        <div className="view resetPassword-view">
          <div className="resetPassword-container">
            <div className="title">Reset password</div>
            <form
              className="resetPassword-form"
              onSubmit={withoutTokenForm.handleSubmit(
                onSubmitHandlerWithoutToken,
              )}
            >
              <CustomInput
                inputProps={{
                  ...withoutTokenForm.register("email"),
                  type: "email",
                  placeholder: "Email",
                  maxLength: "254",
                }}
              />
              {errorMsg && (
                <div className="error-message-container">
                  <span className="error-message">{errorMsg}</span>
                </div>
              )}
              <CustomButton buttonProps={{ type: "submit" }}>
                Send Email
              </CustomButton>
            </form>
          </div>
        </div>
      </>
    );
  }

  return (
    <>
      {isSubmitting && <LoadingSpinner />}

      {token ? resetPasswordWithToken() : resetPasswordWithoutToken()}

      {showPopupWithToken && (
        <CeneteredPopupAlert
          mainContent="Password successfully has been changed"
          closeHandler={() => setShowPopupWithToken(false)}
        />
      )}

      {showPopupWithoutToken && (
        <CeneteredPopupAlert
          mainContent="Email has been sent"
          closeHandler={() => setShowPopupWithoutToken(false)}
          timeout={0}
        />
      )}
    </>
  );
}

export default ResetPasswordView;

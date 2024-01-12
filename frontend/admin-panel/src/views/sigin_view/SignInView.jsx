import React, { useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import Navbar from "../../components/navbar/Navbar";

import "./SignInView.css";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod/dist/zod";
import { z } from "zod";
import axios from "axios";
import CustomInput from "../../components/custom_input/CustomInput";
import CustomButton from "../../components/custom_button/CustomButton";

function SignInView() {
  const [errorMsg, setErrorMsg] = useState("");

  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const redirectTo = searchParams.get("redirect");

  const validationSchema = z.object({
    principal: z.string().min(1, "Principal is required"),
    password: z.string().min(1, "Password is required"),
  });

  const {
    register,
    reset,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(validationSchema),
  });

  const onSubmitHandler = async (data) => {
    try {
      setErrorMsg("");
      await axios.post(import.meta.env.VITE_API_BASE_URL + "/api/auth/login", data, {
        withCredentials: true,
      });
      navigate(redirectTo ? redirectTo : "/", { replace: true });
    } catch (err) {
      const errorRes = err.response;

      if (errorRes?.data?.message) {
        setErrorMsg(errorRes.data.message);
      }
    }

    reset();
  };

  return (
    <>
      <div className="view signin-view">
        <header className="signin-header">
          <Navbar showUserpanel={false} />
        </header>

        <main className="signin-main">
          <div className="signin-container">
            <div className="title">Sign In</div>
            <form
              className="signin-form"
              onSubmit={handleSubmit(onSubmitHandler)}
            >
              <CustomInput
                inputProps={{
                  ...register("principal"),
                  placeholder: "Email or username",
                  maxLength: "254",
                }}
              />
              {errors?.principal && (
                <div className="error-message-container">
                  <span className="error-message">
                    {errors.principal.message}
                  </span>
                </div>
              )}

              <CustomInput
                inputProps={{
                  ...register("password"),
                  type: "password",
                  placeholder: "Password",
                  maxLength: "64",
                }}
              />
              {errors?.password && (
                <div className="error-message-container">
                  <span className="error-message">
                    {errors.password.message}
                  </span>
                </div>
              )}

              {errorMsg && (
                <div className="error-message-container">
                  <span className="error-message">{errorMsg}</span>
                </div>
              )}

              <CustomButton className="test" buttonProps={{ type: "submit" }}>
                Sign In
              </CustomButton>
            </form>
            <div className="signup-text">
              Don't have an account?{" "}
              <Link to="/signup" className="signup-link">
                Sign up
              </Link>
            </div>
            <div className="resetPassword-text">
              <Link to="/resetPassword" className="signup-link">
                Forgot password?
              </Link>
            </div>
          </div>
        </main>
      </div>
    </>
  );
}

export default SignInView;

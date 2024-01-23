import { useState } from "react";
import "./SignUpView.css";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import Navbar from "@components/navbar/Navbar";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod/dist/zod";
import axios from "axios";
import LoadingSpinner from "@components/loading_spinner/LoadingSpinner";
import ErrorMessage from "@components/error_message/ErrorMessage";
import CustomInput from "@shared-components/custom_input/CustomInput";
import CustomButton from "@shared-components/custom_button/CustomButton";

function SignUpView() {
  const [errorMsg, setErrorMsg] = useState("");
  const [errorObj, setErrorObj] = useState({});

  const navigate = useNavigate();

  const validationSchema = z
    .object({
      email: z
        .string()
        .min(1, "Email is required")
        .email("Bad email (example@email.com)"),
      username: z.string().min(1, "Username is required"),
      password: z.string().min(1, "Password is required"),
      confirmPassword: z.string().min(1, "Password is required"),
    })
    .refine((data) => data.password === data.confirmPassword, {
      message: "Password must match",
      path: ["confirmPassword"],
    });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(validationSchema),
  });

  const onSubmitHandler = async (data) => {
    try {
      await axios.post(
        import.meta.env.VITE_API_BASE_URL + "/api/auth/register",
        {
          username: data.username,
          email: data.email,
          password: data.password,
        },
        { withCredentials: true },
      );
      reset();
      setErrorMsg("");
      setErrorObj({});
      navigate("/confirmation");
    } catch (err) {
      const errorRes = err.response;

      if (errorRes.status >= 500) setErrorMsg(errorRes.data);
      else if (typeof errorRes?.data?.message === "string")
        setErrorMsg(errorRes.data.message);
      else if (typeof errorRes?.data?.message === "object")
        setErrorObj(errorRes.data.message);
    }
  };

  return (
    <>
      <div className="view signup-view">
        <header className="signup-header">
          <Navbar showUserpanel={false} />
        </header>

        <main className="signup-main">
          <div className="signup-container">
            <div className="title">Sign Up</div>

            <form
              className="signup-form"
              onSubmit={handleSubmit(onSubmitHandler)}
            >
              <CustomInput
                inputProps={{
                  ...register("email"),
                  placeholder: "Email",
                  maxLength: "254",
                }}
              />
              {errors?.email && (
                <ErrorMessage errorMsg={errors.email.message} />
              )}
              {errorObj?.email && <ErrorMessage errorMsg={errorObj.email} />}

              <CustomInput
                inputProps={{
                  ...register("username"),
                  placeholder: "Username",
                  maxLength: "16",
                }}
              />
              {errors?.username && (
                <ErrorMessage errorMsg={errors.username.message} />
              )}
              {errorObj?.username && (
                <ErrorMessage errorMsg={errorObj.username} />
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
                <ErrorMessage errorMsg={errors.password.message} />
              )}
              {errorObj?.password && (
                <ErrorMessage errorMsg={errorObj.password} />
              )}

              <CustomInput
                inputProps={{
                  ...register("confirmPassword"),
                  type: "password",
                  placeholder: "Confirm password",
                  maxLength: "64",
                }}
              />
              {errors?.confirmPassword && (
                <ErrorMessage errorMsg={errors.confirmPassword.message} />
              )}
              {errorMsg && <ErrorMessage errorMsg={errorMsg} />}

              <CustomButton buttonProps={{ type: "submit" }}>
                Sign Up
              </CustomButton>
            </form>

            <div className="login-text">
              Do you already have an account?{" "}
              <Link to="/signin" className="signin-link">
                Sign in
              </Link>
            </div>
          </div>
        </main>
        {isSubmitting && <LoadingSpinner />}
      </div>
    </>
  );
}

export default SignUpView;

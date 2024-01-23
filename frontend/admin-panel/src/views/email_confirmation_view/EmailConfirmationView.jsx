import axios from "axios";
import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";

import "./EmailConfirmationView.css";
import LoadingSpinner from "@components/loading_spinner/LoadingSpinner";

function EmailConfirmationView() {
  const [searchParams] = useSearchParams();

  const [isSubmitting, setIsSubmitting] = useState(true);
  const [hasConfirmationSucceeded, setHasConfirmationSucceeded] =
    useState(false);
  const [error, setError] = useState({});

  const navigate = useNavigate();

  const token = searchParams.get("token");

  useEffect(() => {
    (async () => {
      if (token) confirmEmail(token);
    })();
  }, []);

  return (
    <>
      {token ? (
        <>
          <div className="view email-confirmation-withtoken-view">
            {isSubmitting && submitting()}
            {hasConfirmationSucceeded && successfulConfirmation()}
            {error.message && unsuccessfulConfirmation()}
          </div>
        </>
      ) : (
        <>
          <div className="view email-confirmation-withouttoken-view">
            <h1>Thank you for registration!</h1>
            <p>Before proceed you should confirm your email address.</p>
            <p>Please check your inbox folder (as well as spam folder)</p>
          </div>
        </>
      )}
    </>
  );

  function submitting() {
    return (
      <>
        <div className="submitting confirmation-container">
          <LoadingSpinner />
        </div>
      </>
    );
  }

  function successfulConfirmation() {
    return (
      <>
        <div className="successful-confirmation-container confirmation-container">
          <h1>Email successfully confirmed!</h1>
        </div>
      </>
    );
  }

  function unsuccessfulConfirmation() {
    return (
      <>
        <div className="unsuccessful-confirmation-container confirmation-container">
          <h1>Invalid token!</h1>
        </div>
      </>
    );
  }

  async function confirmEmail(token) {
    try {
      await axios.get(
        import.meta.env.VITE_API_BASE_URL + "/api/auth/register/confirmation",
        {
          params: { token: token },
          withCredentials: true,
        },
      );

      setHasConfirmationSucceeded(true);
      setIsSubmitting(false);
      setTimeout(() => {
        navigate("/");
      }, 3000);
    } catch (err) {
      setIsSubmitting(false);
      const errorRes = err.response;

      setError(errorRes);
    }
  }
}

export default EmailConfirmationView;

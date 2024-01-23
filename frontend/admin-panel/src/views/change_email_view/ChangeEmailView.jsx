import { useSearchParams } from "react-router-dom";
import "./ChangeEmailView.css";
import { useEffect, useState } from "react";
import axios from "axios";

function ChangeEmailView() {
  const [searchParams] = useSearchParams();
  const [isSubmitted, setIsSubmitted] = useState(false);

  const token = searchParams.get("token");

  useEffect(() => {
    (async () => {
      try {
        await axios.get(import.meta.env.VITE_API_BASE_URL + "/api/auth/changeEmail", {
          params: { token: token },
          withCredentials: true,
        });
        setIsSubmitted(true);
      } catch (err) {}
    })();
  }, []);

  return (
    <>
      {isSubmitted && (
        <h1 style={{ color: "green" }} color="green">
          Email successfully has been changed
        </h1>
      )}
    </>
  );
}

export default ChangeEmailView;

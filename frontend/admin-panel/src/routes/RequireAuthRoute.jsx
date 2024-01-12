import React, { useEffect } from "react";
import { fetchUserDataApiCall } from "../api_calls/UserApiCalls";
import { Navigate, useLocation, useNavigate } from "react-router-dom";

function RequireAuthRoute({ children }) {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    (async () => {
      try {
        await fetchUserDataApiCall();
      } catch (err) {
        return navigate("/signin?redirect=" + location.pathname, {
          replace: true,
        });
      }
    })();
  }, []);

  return children;
}

export default RequireAuthRoute;

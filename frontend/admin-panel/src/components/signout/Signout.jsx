import axios from "axios";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

function Signout() {
  const navigate = useNavigate();

  useEffect(() => {
    (async () => {
      try {
        await axios.post(
          import.meta.env.VITE_API_BASE_URL + "/api/auth/logout",
          {},
          { withCredentials: true },
        );
        navigate("/", { replace: true });
      } catch (err) {}
    })();
  }, []);
}

export default Signout;

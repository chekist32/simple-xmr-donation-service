import axios from "axios";

export async function fetchUserDataApiCall() {
  return await axios.get(import.meta.env.VITE_API_BASE_URL + "/api/user", {
    withCredentials: true,
  });
}

export async function fetchProfileDataApiCall() {
  return await axios.get(import.meta.env.VITE_API_BASE_URL + "/api/user/profile", {
    withCredentials: true,
  });
}

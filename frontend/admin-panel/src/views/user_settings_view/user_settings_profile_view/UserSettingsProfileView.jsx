import { useEffect, useState } from "react";

import "./UserSettingsProfileView.css";
import Avatar from "@shared-components/avatar/Avatar";
import { fetchProfileDataApiCall } from "../../../api_calls/UserApiCalls";
import CustomInput from "@shared-components/custom_input/CustomInput";
import CustomTextarea from "@shared-components/custom_textarea/CustomTextarea";
import CustomButton from "@shared-components/custom_button/CustomButton";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod/dist/zod";
import ErrorMessage from "@components/error_message/ErrorMessage";
import axios from "axios";

function UserSettingsProfileView() {
  const [profileUserData, setProfileUserData] = useState({});

  const [errorMsg, setErrorMsg] = useState("");
  const [errorObj, setErrorObj] = useState({});

  const validationSchema = z.object({
    username: z.string().min(1, "Username is required"),
    greetingText: z.string(),
  });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(validationSchema),
  });

  useEffect(() => {
    (async () => {
      const res = await fetchProfileDataApiCall();
      setProfileUserData(res.data);
    })();
  }, []);

  const onSubmitHandler = async (data) => {
    try {
      setErrorMsg("");
      setErrorObj({});
      const res = await axios.put(
        import.meta.env.VITE_API_BASE_URL + "/api/user/settings/profile",
        data,
        { withCredentials: true },
      );
      setProfileUserData(res.data);
    } catch (err) {
      const errorRes = err.response;

      if (typeof errorRes?.data?.message === "string")
        setErrorMsg(errorRes.data.message);
      else if (typeof errorRes?.data?.message === "object")
        setErrorObj(errorRes.data.message);
    }
  };

  return (
    <div className="default-user-settings-submodule-view-styles user-settings-profile-view">
      <div className="default-user-settings-submodule-view-header-styles user-settings-profile-view-header">
        Your profile
      </div>
      <div className="default-user-settings-submodule-view-main-styles user-settings-profile-view-main">
        <div className="user-settings-profile-view-main-avatar">
          <Avatar
            src="/images/test.png"
            width="150px"
            height="150px"
            hasEditBtn
          />
        </div>
        {Object.keys(profileUserData).length > 0 && (
          <form
            className="user-settings-profile-view-main-form"
            onSubmit={handleSubmit(onSubmitHandler)}
          >
            <div className="user-settings-profile-view-main-username">
              <label>Username:</label>
              <CustomInput
                inputProps={{
                  ...register("username"),
                  placeholder: "Username",
                  maxLength: "64",
                  defaultValue: profileUserData.username,
                }}
              />
              {errors?.username && (
                <ErrorMessage errorMsg={errors.username.message} />
              )}
              {errorObj?.username && (
                <ErrorMessage errorMsg={errorObj.username} />
              )}
            </div>

            <div className="user-settings-profile-view-main-greetingtext">
              <label>Greeting Text:</label>
              <CustomTextarea
                textareaProps={{
                  ...register("greetingText"),
                  placeholder: "Greeting Text",
                  maxLength: "224",
                  defaultValue: profileUserData.greetingText,
                }}
              />
              {errorObj?.greetingText && (
                <ErrorMessage errorMsg={errorObj.greetingText} />
              )}
            </div>

            {errorMsg && <ErrorMessage errorMsg={errorMsg} />}

            <CustomButton buttonProps={{ type: "submit", width: "9rem" }}>
              Save changes
            </CustomButton>
          </form>
        )}
      </div>
    </div>
  );
}

export default UserSettingsProfileView;

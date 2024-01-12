import React, { useEffect, useState } from "react";

import "./UserSettingsDonationView.css";
import "../GlobalUserSettingsSubmoduleStyles.css";
import axios from "axios";
import ReadonlyField from "../../../components/readonly_field/ReadonlyField";
import CustomButton from "../../../components/custom_button/CustomButton";

function UserSettingsDonationView() {
  const [donationSettingsData, setDonationSettingsData] = useState({});

  async function fetchDonationSettingsData() {
    try {
      const res = await axios.get(
        import.meta.env.VITE_API_BASE_URL + "/api/user/settings/donation",
        { withCredentials: true },
      );
      setDonationSettingsData(res.data);
    } catch (err) {}
  }

  async function regenerateToken() {
    try {
      const res = await axios.put(
        import.meta.env.VITE_API_BASE_URL + "/api/user/settings/donation/gennewtoken",
        {},
        { withCredentials: true },
      );
      setDonationSettingsData(res.data);
    } catch (err) {}
  }

  useEffect(() => {
    fetchDonationSettingsData();
  }, []);

  return (
    <div className="default-user-settings-submodule-view-styles-notcentered  user-settings-donation-view">
      <div className="default-user-settings-submodule-view-header-styles user-settings-donation-view-header">
        Donation Settings
      </div>
      <div className="default-user-settings-submodule-view-main-styles user-settings-donation-view-main">
        <div className="user-settings-donation-view-main-token">
          <label>Token:</label>
          {donationSettingsData.userToken && (
            <ReadonlyField id="test" value={donationSettingsData.userToken} />
          )}
          <CustomButton
            buttonProps={{
              type: "submit",
              width: "11rem",
              onClick: regenerateToken,
            }}
          >
            Regenerate token
          </CustomButton>
        </div>

        <div className="user-settings-donation-view-main-donationlink">
          <label>Donation notification link:</label>
          {donationSettingsData.userToken && (
            <ReadonlyField
              value={
                window.location.origin +
                "/notification?token=" +
                donationSettingsData.userToken
              }
            />
          )}
        </div>
      </div>
    </div>
  );
}

export default UserSettingsDonationView;

import { useEffect, useState } from "react";

import "./UserSettingsDonationView.css";
import "../GlobalUserSettingsSubmoduleStyles.css";
import axios from "axios";
import ReadonlyField from "@shared-components/readonly_field/ReadonlyField";
import CustomButton from "@shared-components/custom_button/CustomButton";
import CustomSelect from "@shared-components/custom_select/CustomSelect";
import CustomInput from "@shared-components/custom_input/CustomInput";

function UserSettingsDonationView() {
  const [donationSettingsData, setDonationSettingsData] = useState({});

  async function updateDonationSettingsData() {
    try {
      const res = await axios.put(
        import.meta.env.VITE_API_BASE_URL + "/api/user/settings/donation",
        donationSettingsData,
        { withCredentials: true }
      );
      setDonationSettingsData(res.data);
    } catch (err) { }
  }

  async function fetchDonationSettingsData() {
    try {
      const res = await axios.get(
        import.meta.env.VITE_API_BASE_URL + "/api/user/settings/donation",
        { withCredentials: true }
      );
      setDonationSettingsData(res.data);
    } catch (err) {}
  }

  async function regenerateToken() {
    try {
      const res = await axios.put(
        import.meta.env.VITE_API_BASE_URL + "/api/user/settings/donation/genNewToken",
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

        <div className="user-settings-donation-view-main-confirmationType">
          <label>Confirmation type:</label>
          <div className="user-settings-donation-view-main-confirmationType-select">
            <CustomSelect selectProps={{
              id: "confirmationType",
              value: donationSettingsData.confirmationType,
              onChange: e => setDonationSettingsData({...donationSettingsData, confirmationType: e.target.value})
            }}>
              <option value="UNCONFIRMED">
                Unconfirmed (0)
              </option>
              <option value="PARTIALLY_CONFIRMED">
                Partially confirmed (min 1 block)
              </option>
              <option value="FULLY_CONFIRMED">
                Fully confirmed (min 6-10 blocks)
              </option>
            </CustomSelect>
          </div>
        </div>

        <div className="user-settings-donation-view-main-confirmationType">
          <label>Timeout (min 60s):</label>
            <CustomInput inputProps={{
              value: donationSettingsData.timeout,
              onKeyDown: e => { if(e.key !== "Backspace" && e.key !== "Delete" && !e.key.match(/Arrow*/g) && !e.key.match(/[0-9]/g)) e.preventDefault(); },
              onChange: e => setDonationSettingsData({...donationSettingsData, timeout: e.target.value}) 
            }} />
        </div>

        <div className="user-settings-donation-view-main-minAmount">
          <label>Min amount (in usd):</label>
          <CustomInput inputProps={{
              value: donationSettingsData.minAmount,
              onKeyDown: e => { if(e.key !== "Backspace" && e.key !== "Delete" && !e.key.match(/Arrow*/g) && !e.key.match(/[0-9.]/g)) e.preventDefault(); },
              onChange: e => setDonationSettingsData({...donationSettingsData, minAmount: e.target.value}) 
            }} />
        </div>

        <CustomButton buttonProps={{
          onClick: () => updateDonationSettingsData()
        }}>Save settings</CustomButton>

      </div>
    </div>
  );
}

export default UserSettingsDonationView;

import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

import axios from "axios";

import "./DonationUserPageView.css";

import Invoice from "@components/invoice/Invoice";
import Avatar from "@shared-components/avatar/Avatar";
import CustomInput from "@shared-components/custom_input/CustomInput";
import CustomButton from "@shared-components/custom_button/CustomButton";
import CustomTextarea from "@shared-components/custom_textarea/CustomTextarea";
import CustomSelect from "@shared-components/custom_select/CustomSelect";

function DonationUserPageView() {
  const [donationUserData, setDonationUserData] = useState({});
  const [paymentData, setPaymentData] = useState({});
  const [showInvoice, setShowInvoice] = useState(false);

  const params = useParams();

  useEffect(() => {
    (async () => {
      const res = await axios.get(
        import.meta.env.VITE_API_BASE_URL + "/api/donation/donate/" + params.username,
      );
      setDonationUserData(res.data);
    })();
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();

    const username = document.getElementById("username-field")?.value;
    const donationText = document.getElementById("donationText-field")?.value;
    const coinType = document.getElementById("currencySelect-field")?.value;

    try {
      const res = await axios.post(
        import.meta.env.VITE_API_BASE_URL +
          "/api/donation/donate/" +
          donationUserData.username,
        { senderUsername: username, donationText: donationText, coinType: coinType },
      );
      setPaymentData(res.data);
      setShowInvoice(true);
    } catch (err) {}
  }

  return (
    <div className="view donation-user-page-view">
      <main className="donation-user-page-view-main">
        <div className="donation-page-conatiner">
          <div className="donation-page-header">
            <div>
              <Avatar src="/images/test.png" width="120px" height="120px" />
            </div>
            <div className="donation-page-header-username">
              {donationUserData.username}
            </div>
          </div>
          <div className="donation-page-main">
            <div className="donation-page-main-greeting-text">
              {donationUserData.greetingText}
            </div>
            <div className="donation-page-main-donate-form-container">
              <form
                className="donation-page-main-donate-form"
                onSubmit={handleSubmit}
              >
                <CustomInput
                  inputProps={{
                    placeholder: "Your name",
                    maxLength: "64",
                    id: "username-field",
                  }}
                />
                <CustomTextarea
                  textareaProps={{
                    maxLength: "300",
                    rows: "10",
                    id: "donationText-field",
                    placeholder: "Your message",
                  }}
                />
                <CustomSelect selectProps={{
                  id: "currencySelect-field"
                }}>
                  <option value="XMR">XMR</option>
                </CustomSelect>
                <CustomButton buttonProps={{ type: "submit" }}>
                  Send
                </CustomButton>
              </form>
            </div>
            {showInvoice && (
              <Invoice
                paymentData={paymentData}
                closeHandler={() => setShowInvoice(false)}
              />
            )}
          </div>
        </div>
      </main>
    </div>
  );
}

export default DonationUserPageView;

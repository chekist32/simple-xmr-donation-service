import React, { useEffect, useState } from "react";

import "./NotificationView.css";
import { useSearchParams } from "react-router-dom";

function NotificationView() {
  const donationQueue = [];
  const [showFlag, setShowFlag] = useState(false);
  const [donationData, setDonationData] = useState({});
  const [searchParams, setSearchParams] = useSearchParams();

  const token = searchParams.get("token");

  let isViewDonationsResolved = true;

  async function viewDonation(donationData) {
    if (!donationData) return;
    setDonationData(donationData);
    setShowFlag(true);
  }

  async function viewDonations() {
    return new Promise((resolve) => {
      const intervalId = setInterval(() => {
        if (donationQueue.length > 0) {
          viewDonation(donationQueue.shift());
        } else {
          setShowFlag(false);
          clearInterval(intervalId);
          resolve();
        }
      }, 5000);
    });
  }

  useEffect(() => {
    const sse = new EventSource(
      import.meta.env.VITE_API_BASE_URL + "/api/donation/emitter?token=" + token,
      { withCredentials: true },
    );

    sse.onmessage = (res) => {
      donationQueue.push(JSON.parse(res.data));

      if (isViewDonationsResolved) {
        isViewDonationsResolved = false;
        viewDonations().then(() => (isViewDonationsResolved = true));
      }
    };

    return () => sse.close();
  }, []);

  return (
    <>
      <div className="notification-view">
        {showFlag && donationCardRender(donationData)}
      </div>
    </>
  );
}

function donationCardRender(donationData) {
  return (
    <>
      <div className="donation-vew-donation-card">
        <div className="monerochan-container">
          <img className="monerochan" src="/images/monerochan.png" alt="" />
        </div>

        <div className="donation-vew-donation-card-username">
          {donationData && donationData.username}
        </div>

        <div className="donation-vew-donation-card-amount">
          {donationData && donationData.amount}
        </div>

        <div className="donation-vew-donation-card-donationText">
          {donationData && donationData.donationText}
        </div>
      </div>
    </>
  );
}

export default NotificationView;

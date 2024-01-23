import { useEffect, useState } from "react";
import Navbar from "@components/navbar/Navbar";

import "./UserProfileView.css";
import { fetchProfileDataApiCall } from "../../api_calls/UserApiCalls";
import UserProfile from "@components/user_profile/UserProfile";

function UserProfileView() {
  const [donationUserData, setDonationUserData] = useState({});

  useEffect(() => {
    (async () => {
      const res = await fetchProfileDataApiCall();
      setDonationUserData(res.data);
    })();
  }, []);

  return (
    <div className="view user-profile-view">
      <header className="user-profile-view-header">
        <Navbar showUserpanel />
      </header>
      <main className="user-profile-view-main">
        <UserProfile donationUserData={donationUserData} />
      </main>
    </div>
  );
}

export default UserProfileView;

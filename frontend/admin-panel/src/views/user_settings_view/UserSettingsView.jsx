import React, { useEffect, useState } from "react";

import "./UserSettingsView.css";
import Navbar from "../../components/navbar/Navbar";
import Avatar from "../../components/avatar/Avatar";
import { fetchUserDataApiCall } from "../../api_calls/UserApiCalls";
import { Link, Outlet } from "react-router-dom";

function UserSettingsView({ parentEndpoint }) {
  const [userData, setUserData] = useState({});

  useEffect(() => {
    (async () => {
      const res = await fetchUserDataApiCall();
      setUserData(res.data);
    })();
  }, []);

  return (
    <div className="view user-settings-view">
      <header className="user-settings-view-header">
        <Navbar showUserpanel />
      </header>
      <main className="user-settings-view-main">
        <aside className="user-settings-view-settings-list">
          <div className="user-settings-view-settings-list-header">
            <Avatar src="/images/test.png" height="50px" width="50px" />
            <div className='"user-settings-view-settings-list-header-username'>
              {userData.username}
            </div>
          </div>

          <Link to="profile">
            <div className="user-settings-view-settings-list-item">
              Your Profile
            </div>
          </Link>

          <Link to="account">
            <div className="user-settings-view-settings-list-item">Account</div>
          </Link>

          <Link to="security">
            <div className="user-settings-view-settings-list-item">
              Security
            </div>
          </Link>

          <Link to="donation">
            <div className="user-settings-view-settings-list-item">
              Donation Settings
            </div>
          </Link>
        </aside>

        <div className="user-settings-view-settings-content">
          <Outlet />
        </div>
      </main>
    </div>
  );
}

export default UserSettingsView;

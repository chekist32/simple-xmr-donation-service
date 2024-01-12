import axios from "axios";
import React, { useState, useEffect } from "react";
import styles from "./UserPanel.module.css";
import { Link } from "react-router-dom";
import {
  getUserStorage,
  setUserStorage,
} from "../../storage/CustomUserStorage";
import UserMenu from "../user_menu/UserMenu";

function UserPanel() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(true);

  const [isSideUserPanelShown, setIsSideUserPanelShown] = useState(false);

  const cachedUser = getUserStorage();

  const [userData, setUserData] = useState(cachedUser);

  const closeSideUserPanelHandler = () => {
    setIsSideUserPanelShown(false);
  };

  useEffect(() => {
    (async () => {
      try {
        const res = await axios.get(import.meta.env.VITE_API_BASE_URL + "/api/user", {
          withCredentials: true,
        });

        const userData = await res.data;

        setUserData(userData);
        setIsLoggedIn(true);
        setIsSubmitting(false);
        setUserStorage(userData);
      } catch (err) {
        setIsSubmitting(false);
      }
    })();
  }, []);

  return <>{!isSubmitting && renderUserPanel()}</>;

  function renderUserPanel() {
    return <>{isLoggedIn ? AuthorizedUserPanel() : UnauthorizedUserPanel()}</>;
  }

  function UnauthorizedUserPanel() {
    return (
      <>
        <div className={styles.unauthorized_user_panel}>
          <Link className={styles.unauthorized_user_panel__link} to="/signin">
            Sign in
          </Link>
          <Link className={styles.unauthorized_user_panel__link} to="/signup">
            Sign up
          </Link>
        </div>
      </>
    );
  }
  function AuthorizedUserPanel() {
    return (
      <>
        <div className={styles.authorized_user_panel}>
          <div
            className={styles.authorized_user_panel__username}
            onClick={() => setIsSideUserPanelShown(!isSideUserPanelShown)}
          >
            {userData.username}
          </div>
          {isSideUserPanelShown && (
            <UserMenu closeHandler={closeSideUserPanelHandler} />
          )}
        </div>
      </>
    );
  }
}

export default UserPanel;

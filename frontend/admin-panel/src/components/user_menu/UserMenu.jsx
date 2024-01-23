import PropTypes from 'prop-types';

import { useEffect, useState } from "react";
import AsidePanel from "@components/aside_panel/AsidePanel";

import styles from "./UserMenu.module.css";
import { getUserStorage } from "../../storage/CustomUserStorage";
import { fetchUserDataApiCall } from "../../api_calls/UserApiCalls";

import { Link } from "react-router-dom";

function UserMenu({ closeHandler }) {
  const [userData, setUserData] = useState({});

  useEffect(() => {
    const cachedUser = getUserStorage();
    cachedUser ? setUserData(cachedUser) : fetchUserData();
  }, []);

  async function fetchUserData() {
    try {
      const res = await fetchUserDataApiCall();
      const data = await res.data;
      setUserData(data);
    } catch (err) {}
  }

  const Menu = () => {
    return (
      <>
        <div className={styles.menu_container}>
          <div className={styles.header}>
            <div className="username">{userData.username}</div>
            <div className={styles.close_menu_btn} onClick={closeHandler}>
              <svg
                width="10"
                height="10"
                viewBox="0 0 50 50"
                overflow="visible"
                stroke="white"
                strokeWidth="5"
                strokeLinecap="square"
              >
                <line x2="50" y2="50" />
                <line x1="50" y2="50" />
              </svg>
            </div>
          </div>
          <div className={styles.main}>
            <div className={styles.action_list}>
              <Link to="/profile">
                <div className={styles.action_list_item}>Profile</div>
              </Link>
              <Link to="/settings">
                <div className={styles.action_list_item}>Settings</div>
              </Link>
              <Link to="/signout">
                <div className={styles.action_list_item}>Sign out</div>
              </Link>
            </div>
          </div>
        </div>
      </>
    );
  };

  return (
    <AsidePanel position="right" closeHandler={closeHandler} content={Menu()} />
  );
}

UserMenu.propTypes = {
  closeHandler: PropTypes.func.isRequired
}

export default UserMenu;

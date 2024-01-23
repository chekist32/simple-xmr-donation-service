import PropTypes from 'prop-types';

import styles from "./UserProfile.module.css";

import Avatar from "@shared-components/avatar/Avatar";

function UserProfile({ donationUserData, height, width }) {
  const containerStyles = {};
  typeof height === "string" ? (containerStyles.height = height) : null;
  typeof width === "string" ? (containerStyles.width = width) : null;

  return (
    <>
      <div
        className={styles.user_profile_conatiner}
        style={Object.entries(containerStyles).length ? containerStyles : null}
      >
        <div className={styles.user_profile_header}>
          <div>
            <Avatar src="/images/test.png" width="120px" height="120px" />
          </div>
          <div className={styles.user_profile_header_username}>
            {donationUserData.username}
          </div>
        </div>
        <div className={styles.user_profile_main}>
          <div className={styles.user_profile_main_greeting_text}>
            {donationUserData.greetingText}
          </div>
        </div>
      </div>
    </>
  );
}

UserProfile.propTypes = {
  height: PropTypes.string,
  width: PropTypes.string,
  donationUserData: PropTypes.shape({
      username: PropTypes.string, 
      greetingText: PropTypes.string
    })
}

export default UserProfile;

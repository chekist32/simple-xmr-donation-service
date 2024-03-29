import PropTypes from 'prop-types';

import { useState } from "react";

import styles from "./Avatar.module.css";
import CustomButton from "@shared-components/custom_button/CustomButton";

function Avatar({ src, width, height, hasEditBtn }) {
  const [username, setUsername] = useState("user");

  const imgWidth = width ? width : "100px";
  const imgHeight = height ? height : "100px";

  return (
    <>
      <div
        className={styles.avatar_container}
        style={{ width: imgWidth, height: imgHeight }}
      >
        {src ? (
          <img className={styles.avatar} src={src} />
        ) : (
          <div
            style={{ backgroundColor: "red", width: "100%", height: "100%" }}
          >
            {username.charAt(0) + " " + username.charAt(1)}
          </div>
        )}
        {hasEditBtn && (
          <CustomButton
            id={styles.editBtn}
            buttonProps={{ height: "1.7rem", width: "3.2rem" }}
          >
            {" "}
            Edit
          </CustomButton>
        )}
      </div>
    </>
  );
}

Avatar.propTypes = {
  src: PropTypes.string,
  width: PropTypes.string,
  height: PropTypes.string,
  hasEditBtn: PropTypes.bool
}

export default Avatar;

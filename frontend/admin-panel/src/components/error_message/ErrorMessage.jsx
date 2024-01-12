import React from "react";

import styles from "./ErrorMessage.module.css";

function ErrorMessage({ errorMsg }) {
  return (
    <>
      <div className={styles.error_message_container}>
        <span className={styles.error_message}>{errorMsg}</span>
      </div>
    </>
  );
}

export default ErrorMessage;

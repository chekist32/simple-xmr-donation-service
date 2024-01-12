import React from "react";

import styles from "./LoadingSpinner.module.css";

function LoadingSpinner() {
  return (
    <>
      <div className={styles.loading_spinner_container}>
        <div className={styles.loading_spinner}></div>
      </div>
    </>
  );
}

export default LoadingSpinner;

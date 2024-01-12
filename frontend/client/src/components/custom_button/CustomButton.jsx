import React from "react";

import styles from "./CustomButton.module.css";

function CustomButton({ buttonProps, children, id }) {
  const height = buttonProps?.height ? buttonProps.height : "2.5rem";
  const width = buttonProps?.width ? buttonProps.width : "12rem";

  return (
    <button
      className={styles.button}
      id={id}
      {...buttonProps}
      style={{ width: width, height: height }}
    >
      {children}
    </button>
  );
}

export default CustomButton;

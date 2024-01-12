import React, { useEffect, useState } from "react";

import styles from "./ReadonlyField.module.css";

function ReadonlyField({ value }) {
  const checkIconClassName = "fa fa-check";
  const clipboardIconClassName = "fa fa-clipboard";

  const [copyBtnClassName, setCopyBtnClassName] = useState([
    clipboardIconClassName,
  ]);

  async function copyToClipboardHandler() {
    try {
      await navigator.clipboard.writeText(value);
      setCopyBtnClassName(checkIconClassName);
      setTimeout(() => setCopyBtnClassName(clipboardIconClassName), 2000);
    } catch (err) {}
  }

  return (
    <div className={styles.readonly_container_wrapper}>
      <div className={styles.readonly_container}>{value}</div>
      <i
        className={copyBtnClassName}
        id={styles.copyBtn}
        style={{
          color:
            copyBtnClassName == clipboardIconClassName ? "gray" : "#03c04a",
        }}
        aria-hidden="true"
        onClick={copyToClipboardHandler}
      ></i>
    </div>
  );
}

export default ReadonlyField;

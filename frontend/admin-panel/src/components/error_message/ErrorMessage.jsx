import PropTypes from 'prop-types';

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

ErrorMessage.propTypes = {
  errorMsg: PropTypes.oneOfType([PropTypes.element, PropTypes.string])
}

export default ErrorMessage;

import PropTypes from 'prop-types';

import QRCode from "react-qr-code";

import styles from "./Invoice.module.css";
import { useEffect, useState } from "react";
import axios from "axios";
import ReadonlyField from '@shared-components/readonly_field/ReadonlyField';

function Invoice({ paymentData, timeout, closeHandler }) {
  const [msg, setMsg] = useState("");
  const [status, setStatus] = useState("none");

  useEffect(() => {
    (async () => {
      try {
        const res = await axios.get(
          import.meta.env.VITE_API_BASE_URL +
            "/api/payment/" +
            paymentData.paymentId +
            "/status",
        );
        setMsg(res.data);
        setStatus("success");
      } catch (err) {
        setStatus("failure");
        setMsg(err.response.data);
      }
    })();
  }, []);

  function success() {
    return (
      <div className={styles.invoice_container}>
        <div className={styles.invoice}>
          <div className={styles.header}>
            <div id={styles.closeBtn} onClick={closeHandler}>
              <svg
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M16.3394 9.32245C16.7434 8.94589 16.7657 8.31312 16.3891 7.90911C16.0126 7.50509 15.3798 7.48283 14.9758 7.85938L12.0497 10.5866L9.32245 7.66048C8.94589 7.25647 8.31312 7.23421 7.90911 7.61076C7.50509 7.98731 7.48283 8.62008 7.85938 9.0241L10.5866 11.9502L7.66048 14.6775C7.25647 15.054 7.23421 15.6868 7.61076 16.0908C7.98731 16.4948 8.62008 16.5171 9.0241 16.1405L11.9502 13.4133L14.6775 16.3394C15.054 16.7434 15.6868 16.7657 16.0908 16.3891C16.4948 16.0126 16.5171 15.3798 16.1405 14.9758L13.4133 12.0497L16.3394 9.32245Z"
                  fill="currentColor"
                />
                <path
                  fillRule="evenodd"
                  clipRule="evenodd"
                  d="M1 12C1 5.92487 5.92487 1 12 1C18.0751 1 23 5.92487 23 12C23 18.0751 18.0751 23 12 23C5.92487 23 1 18.0751 1 12ZM12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12C21 16.9706 16.9706 21 12 21Z"
                  fill="currentColor"
                />
              </svg>
            </div>
          </div>
          <div className={styles.status}>
            <img
              className={styles.image_status}
              src="/images/success.png"
              alt=""
            />
            <div className={styles.message}>
              {msg ? msg : "Payment has been confirmed"}
            </div>
          </div>
          <div className={styles.paymentId}>
            Payment id: {paymentData.paymentId}
          </div>
        </div>
      </div>
    );
  }

  function failure() {
    return (
      <div className={styles.invoice_container}>
        <div className={styles.invoice}>
          <div className={styles.header}>
            <div id={styles.closeBtn} onClick={closeHandler}>
              <svg
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M16.3394 9.32245C16.7434 8.94589 16.7657 8.31312 16.3891 7.90911C16.0126 7.50509 15.3798 7.48283 14.9758 7.85938L12.0497 10.5866L9.32245 7.66048C8.94589 7.25647 8.31312 7.23421 7.90911 7.61076C7.50509 7.98731 7.48283 8.62008 7.85938 9.0241L10.5866 11.9502L7.66048 14.6775C7.25647 15.054 7.23421 15.6868 7.61076 16.0908C7.98731 16.4948 8.62008 16.5171 9.0241 16.1405L11.9502 13.4133L14.6775 16.3394C15.054 16.7434 15.6868 16.7657 16.0908 16.3891C16.4948 16.0126 16.5171 15.3798 16.1405 14.9758L13.4133 12.0497L16.3394 9.32245Z"
                  fill="currentColor"
                />
                <path
                  fillRule="evenodd"
                  clipRule="evenodd"
                  d="M1 12C1 5.92487 5.92487 1 12 1C18.0751 1 23 5.92487 23 12C23 18.0751 18.0751 23 12 23C5.92487 23 1 18.0751 1 12ZM12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12C21 16.9706 16.9706 21 12 21Z"
                  fill="currentColor"
                />
              </svg>
            </div>
          </div>
          <div className={styles.status}>
            <img
              className={styles.image_status}
              src="/images/failure.png"
              alt=""
            />
            <div className={styles.message}>
              {msg ? msg : "Error has occured"}
            </div>
          </div>
          <div className={styles.paymentId}>
            Payment id: {paymentData.paymentId}
          </div>
        </div>
      </div>
    );
  }

  function none() {
    return (
      <div className={styles.invoice_container}>
        <div className={styles.invoice}>
          <div className={styles.invoice_header}>Your invoice</div>
          <div className={styles.amount}>
            Min amount:{" "}
            {paymentData.minAmount ? paymentData.minAmount : "0.01 XMR"}
          </div>
          <div className={styles.subaddress}>
            <label htmlFor="">Address</label>
            <ReadonlyField value={paymentData.subaddress} />
          </div>
          <div className={styles.qr_code_container}>
            <QRCode
              style={{
                maxWidth: "256px",
                width: "100%",
                maxHeight: "256px",
                height: "80%",
              }}
              value={paymentData.subaddress}
            />
          </div>
          <div className={styles.paymentId}>
            Payment id: {paymentData.paymentId}
          </div>
        </div>
      </div>
    );
  }

  return (
    <>
      {status === "none" && none()}
      {status === "success" && success()}
      {status === "failure" && failure()}
    </>
  );
}

Invoice.propTypes = {
  timeout: PropTypes.number,
  closeHandler: PropTypes.func.isRequired,
  paymentData: PropTypes.shape({
    paymentId: PropTypes.oneOf([PropTypes.number, PropTypes.string]),
    minAmount: PropTypes.oneOf([PropTypes.string, PropTypes.number]),
    subaddress: PropTypes.string
  })
}

export default Invoice;

import styles from "./CustomInput.module.css";

function CustomInput({ inputProps }) {
  return (
    <div className={styles.input_container}>
      <input className={styles.input} {...inputProps} />
    </div>
  );
}

export default CustomInput;

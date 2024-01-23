import styles from "./CustomTextarea.module.css";

function CustomTextarea({ textareaProps }) {
  return (
    <div className={styles.textarea_container}>
      <textarea className={styles.textarea} {...textareaProps} />
    </div>
  );
}

export default CustomTextarea;

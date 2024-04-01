import styles from "./CustomSelect.module.css";


function CustomSelect({ selectProps, children }) {
    return (
      <div className={styles.select_container}>
        <select className={styles.select} {...selectProps}>
            {children}
        </select>
      </div>
    );
  }
  
  export default CustomSelect;
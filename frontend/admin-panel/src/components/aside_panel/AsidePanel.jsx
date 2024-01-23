import { useRef } from "react";
import PropTypes from 'prop-types';

import styles from "./AsidePanel.module.css";

function AsidePanel({ content, position, closeHandler }) {
  const containerClassNames = `
    ${styles.aside_panel_container} 
    ${position === "left" ? styles.left : styles.right}
    `;

  const asidePanel = useRef(null);

  const handleClick = (e) => {
    const element = e.target;
    if (asidePanel.current && !asidePanel.current.contains(element)) {
      e.preventDefault();
      e.stopPropagation();
      closeHandler();
    }
  };

  return (
    <>
      <div className={containerClassNames} onClick={handleClick}>
        <div className={styles.aside_panel} ref={asidePanel}>
          {content}
        </div>
      </div>
    </>
  );
}

AsidePanel.propTypes = {
  content: PropTypes.oneOfType([PropTypes.element, PropTypes.string]),
  position: PropTypes.string,
  closeHandler: PropTypes.func.isRequired
}

export default AsidePanel;




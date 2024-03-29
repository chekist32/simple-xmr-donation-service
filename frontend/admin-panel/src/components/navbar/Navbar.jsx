import PropTypes from 'prop-types';

import { Link } from "react-router-dom";
import UserPanel from "@components/user_panel/UserPanel";

import styles from "./Navbar.module.css";

function Navbar({ showUserpanel }) {
  return (
    <>
      <nav className={styles.navbar}>
        <Link className={styles.navbar_logo_container} to="/">
          <img
            className={styles.navbar_logo}
            src="/images/monerochan.png"
            alt=""
          />
        </Link>
        <div className={styles.links}>
          <Link className={styles.link} to="/donations">
            Donations
          </Link>
        </div>
        <div className={styles.user_panel}>
          {showUserpanel && <UserPanel />}
        </div>
      </nav>
    </>
  );
}

Navbar.propTypes = {
  showUserpanel: PropTypes.bool
}

export default Navbar;

import { useEffect, useState } from "react";
import styles from './TimeoutBar.module.css'
import { array } from "prop-types";


function TimeoutBar({ timeout, text }) {
    const [timeLeft, setTimeLeft] = useState(timeout/1000);
    const [clock, setClock] = useState(parseSecondsToClock(timeLeft));

    useEffect(() => {
        setInterval(() => {
            setTimeLeft(prev => prev - 1);
            setClock(prev => decrementClock(prev));
        }, 1000);
    }, []);

    function decrementClock(clockStr) {
        const parseUnit = (unit) => {
            return typeof(unit) !== 'number' || unit > 9 ? unit : "0"+unit;
        }

        let units = clockStr.split(':');
        
        for (let index = units.length-1, unit = units[index] = parseInt(units[index])-1; 
            index > 0 && unit < 0; 
            index--) { 
                unit = units[index-1] = parseInt(units[index-1])-1
                units[index] += 60;  
            }

        return units.map(unit => parseUnit(unit)).join(':');
    }

    function parseSecondsToClock(sec) {
        const parseUnit = (unit) => {
            return unit > 9 ? unit : "0"+unit;
        }

        if (sec > 3600) {
            let hours = sec / 3600;
            let minutes = (hours - (hours = Math.floor(hours))) * 60;
            let seconds = Math.floor((minutes - (minutes = Math.floor(minutes))) * 60);

            return parseUnit(hours)+":"+parseUnit(minutes)+":"+parseUnit(seconds);
            
        }
        else if (sec > 60) {
            let minutes = sec / 60;
            let seconds = Math.floor((minutes - (minutes = Math.floor(minutes))) * 60);

            return parseUnit(minutes)+":"+parseUnit(seconds);
        }
        else return "00:"+parseUnit(sec);
    }

    return (
      <div className={styles.timeout_bar_container}>
        <div className={styles.timeout_bar} style={{width: (1-timeLeft/timeout*1000)*100+"%"}}><div>{text}</div></div>
        <div className={styles.clock}>{clock}</div> 
      </div>
    );
  }
  
  export default TimeoutBar;
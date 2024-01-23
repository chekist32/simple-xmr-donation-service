import Navbar from "@components/navbar/Navbar";
import "./HomeView.css";

function HomeView() {
  return (
    <>
      <div className="view home-view">
        <header className="header">
          <Navbar showUserpanel />
        </header>

        <main className="main">
          <h2>Monero donation service</h2>
          <div className="monerochan-container">
            <img className="monerochan" src="/images/monerochan.png" alt="" />
          </div>
        </main>
      </div>
    </>
  );
}

export default HomeView;

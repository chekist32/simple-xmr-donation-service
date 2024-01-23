import { useEffect, useState } from "react";
import ReactPaginate from "react-paginate";

import "./DonationView.css";

import axios from "axios";
import Navbar from "@components/navbar/Navbar";

function DonationView() {
  const [data, setData] = useState([]);
  const [donationCount, setDonationCount] = useState(0);
  const [pageCount, setPageCount] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  function renderDonationItems(items) {
    return (
      <>
        {items.map((item, i) => (
          <div key={i} className="donation-view-donation-item">
            <div className="donation-view-donation-item-header">
              <div className="donation-view-donation-item-header">
                <div className="donation-view-donation-item-header-username">
                  {item.username}
                </div>
                <div className="donation-view-donation-item-grey">donated</div>
                <div className="donation-view-donation-item-header-amount">
                  {item.amount}
                </div>
              </div>
              <div className="donation-view-donation-item-grey donation-view-donation-item-header-confirmedAt">
                {item.confirmedAt}
              </div>
            </div>
            <div className="donation-view-donation-item-donationtext">
              {item.donationText}
            </div>
            <br />
          </div>
        ))}
      </>
    );
  }

  async function handlePageClick(event) {
    try {
      const res1 = await axios.get(
        import.meta.env.VITE_API_BASE_URL + "/api/donation",
        {
          withCredentials: true,
          params: { page: event.selected, itemsPerPage: itemsPerPage },
        },
      );
      setData(res1.data);

      const res2 = await axios.get(
        import.meta.env.VITE_API_BASE_URL + "/api/donation/count",
        {
          withCredentials: true,
        },
      );
      setDonationCount(res2.data);
    } catch (err) {}
  }

  useEffect(() => {
    setPageCount(Math.ceil(donationCount / itemsPerPage));
  }, [donationCount, itemsPerPage]);

  useEffect(() => {
    (async () => {
      try {
        const res = await axios.get(
          import.meta.env.VITE_API_BASE_URL + "/api/donation",
          {
            withCredentials: true,
            params: { page: 0, itemsPerPage: itemsPerPage },
          },
        );
        setData(res.data);

        const res2 = await axios.get(
          import.meta.env.VITE_API_BASE_URL + "/api/donation/count",
          {
            withCredentials: true,
          },
        );
        setDonationCount(res2.data);
      } catch (err) {}
    })();
  }, []);

  return (
    <>
      <div className="view donation-view">
        <header className="header">
          <Navbar showUserpanel />
        </header>
        <div className="donation-view-donations">
          {data && renderDonationItems(data)}
        </div>
        <div className="donation-view-pagination">
          <ReactPaginate
            breakLabel="..."
            nextLabel=">"
            onPageChange={handlePageClick}
            pageRangeDisplayed={3}
            pageCount={pageCount}
            previousLabel="<"
            renderOnZeroPageCount={null}
            containerClassName="donation-view-pagination"
            pageClassName="donation-view-page-num"
            pageLinkClassName="donation-view-page-num-link"
            previousLinkClassName="donation-view-arrows donation-view-prev-link"
            nextLinkClassName=" donation-view-arrows donation-view-next-link"
            activeLinkClassName="donation-view-active-link"
          />
        </div>
      </div>
    </>
  );
}

export default DonationView;

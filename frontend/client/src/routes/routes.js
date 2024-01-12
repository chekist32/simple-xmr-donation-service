import { createBrowserRouter } from "react-router-dom";
import DonationUserPageView from "../views/donation_view/DonationUserPageView";
import NotFoundView from "../views/notfound_view/NotFoundView";
import InvoiceView from "../views/invoice_view/InvoiceView";

const router = createBrowserRouter([
  {
    path: "/donate/:username",
    Component: DonationUserPageView,
  },
  // {
  //     path: "/payment/:paymentId",
  //     Component: InvoiceView
  // }
  {
    path: "*",
    Component: NotFoundView,
  },
]);

export default router;

import { createBrowserRouter } from "react-router-dom";
import DonationUserPageView from "@views/donation_view/DonationUserPageView";
import NotFoundView from "@shared-views/notfound_view/NotFoundView";

const router = createBrowserRouter([
  {
    path: "/donate/:username",
    Component: DonationUserPageView,
  },
  {
    path: "*",
    Component: NotFoundView,
  },
]);

export default router;

import { createBrowserRouter } from "react-router-dom";
import HomeView from "../views/home_view/HomeView";
import NotFoundView from "../views/notfound_view/NotFoundView";
import SignInView from "../views/sigin_view/SignInView";
import SignUpView from "../views/signup_view/SignUpView";
import EmailConfirmationView from "../views/email_confirmation_view/EmailConfirmationView";
import UserProfileView from "../views/user_profile_view/UserProfileView";
import UserSettingsView from "../views/user_settings_view/UserSettingsView";
import RequireAuthRoute from "./RequireAuthRoute";
import React from "react";
import UserSettingsProfileView from "../views/user_settings_view/user_settings_profile_view/UserSettingsProfileView";
import UserSettingsAccountView from "../views/user_settings_view/user_settings_account_view/UserSettingsAccountView";
import UserSettingsSecurityView from "../views/user_settings_view/user_settings_security_view/UserSettingsSecurityView";
import UserSettingsDonationView from "../views/user_settings_view/user_settings_donation_view/UserSettingsDonationView";
import Signout from "../components/signout/Signout";
import NotificationView from "../views/notification_view/NotificationView";
import ChangeEmailView from "../views/change_email_view/ChangeEmailView";
import ResetPasswordView from "../views/reset_password_view/ResetPasswordView";
import DonationView from "../views/donation_view/DonationView";

const router = createBrowserRouter([
  {
    path: "/",
    Component: HomeView,
  },
  {
    path: "/signin",
    Component: SignInView,
  },
  {
    path: "/signup",
    Component: SignUpView,
  },
  {
    path: "/confirmation",
    Component: EmailConfirmationView,
  },
  {
    path: "/notfound",
    Component: NotFoundView,
  },
  {
    path: "/profile",
    Component: () => {
      return (
        <RequireAuthRoute>
          <UserProfileView />
        </RequireAuthRoute>
      );
    },
  },
  {
    path: "/settings",
    Component: () => {
      return (
        <RequireAuthRoute>
          <UserSettingsView parentEndpoint={"/settings"} />
        </RequireAuthRoute>
      );
    },
    children: [
      {
        path: "profile",
        Component: UserSettingsProfileView,
      },
      {
        path: "account",
        Component: UserSettingsAccountView,
      },
      {
        path: "security",
        Component: UserSettingsSecurityView,
      },
      {
        path: "donation",
        Component: UserSettingsDonationView,
      },
    ],
  },
  {
    path: "/signout",
    Component: Signout,
  },
  {
    path: "/notification",
    // Component: () => {
    //     return (
    //         <RequireAuthRoute>
    //             <NotificationView />
    //         </RequireAuthRoute>
    //     )
    // }
    Component: NotificationView,
  },
  {
    path: "/changeEmail",
    Component: ChangeEmailView,
  },
  {
    path: "/resetPassword",
    Component: ResetPasswordView,
  },
  {
    path: "/donations",
    Component: () => {
      return (
        <RequireAuthRoute>
          <DonationView />
        </RequireAuthRoute>
      );
    },
  },
  {
    path: "*",
    Component: NotFoundView,
  },
]);

export default router;

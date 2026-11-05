import React from "react";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import ReactDOM from "react-dom/client";
import "./index.css";
import Layout from "./layout/Layout.tsx";
import ErrorPage from "./pages/error/ErrorPage.tsx";
import { Dashboard } from "./pages/dashboard/Dashboard.tsx";
import { Upload } from "./pages/upload/Upload.tsx";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: "/",
        element: <Dashboard />,
      },
      {
        path: "/upload",
        element: <Upload />,
      },
    ],
  },
]);

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>,
);

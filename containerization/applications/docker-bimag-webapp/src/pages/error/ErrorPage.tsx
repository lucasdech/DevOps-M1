import { Box, Typography } from "@mui/material";
import { useRouteError } from "react-router-dom";

const ErrorPage = () => {
  const error = useRouteError() as Error;
  console.error(error);

  return (
    <Box id="error-page">
      <Typography>Oops!</Typography>
      <Typography>Sorry, an unexpected error has occurred.</Typography>
      <Typography>
        <Typography>{error?.message || error?.name}</Typography>
      </Typography>
    </Box>
  );
};

export default ErrorPage;

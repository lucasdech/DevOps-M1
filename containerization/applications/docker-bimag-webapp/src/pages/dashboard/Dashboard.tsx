import { Box, Button, Typography } from "@mui/material";
import { Link } from "react-router-dom";
import "./Dashboard.css";
import Joker from "../../assets/joker_1.webp";

export const Dashboard = () => {
  return (
    <Box>
      <Box>
        <Typography variant="h2">Bienvenu à Bimag !</Typography>
        <Typography variant="h5" sx={{ marginTop: "4rem" }}>
          Modificateur d'images
        </Typography>
        <Box
          className="rotate-image"
          component="img"
          src={Joker}
          alt="Example image"
          sx={{
            width: "100%",
            height: 100,
            objectFit: "contain",
            marginTop: "3rem",
          }}
        />
        <Box sx={{ marginTop: "3rem" }}>
          <Link to={"/upload"}>
            <Button variant="outlined">J'y vais</Button>
          </Link>
        </Box>
      </Box>
    </Box>
  );
};

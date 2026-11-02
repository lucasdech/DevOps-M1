import { useState, useEffect } from "react";
import {
  Box,
  Button,
  Paper,
  Typography,
  Select,
  MenuItem,
  CircularProgress,
} from "@mui/material";
import { uploadImage, processImage, getImage } from "../../services/service";
import "./Upload.css";

export const Upload = () => {
  const [file, setFile] = useState<File | null>(null);
  const [preview, setPreview] = useState("");
  const [size, setSize] = useState(1080);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (!file) return;
    const url = URL.createObjectURL(file);
    setPreview(url);
    return () => URL.revokeObjectURL(url);
  }, [file]);

  const resize = async () => {
    setSuccess(false);
    if (!file || loading) return;
    setLoading(true);
    setError("");
    try {
      const id = (await uploadImage(file)).trim();
      const processedId = (await processImage(id, size)).trim();
      const blob = await getImage(processedId);
      const url = URL.createObjectURL(blob);
      setPreview(url);
      setSuccess(true);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Processing failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: 3, display: "flex", gap: 3, flexWrap: "wrap" }}>
      <Paper sx={{ p: 3, flex: 1, minWidth: 280 }}>
        <Typography variant="h5">Upload Image</Typography>
        <Button component="label" variant="outlined" sx={{ my: 2 }}>
          Select Image
          <input
            hidden
            type="file"
            accept="image/*"
            onChange={(e) => {
              setFile(e.target.files?.[0] ?? null);
              setError("");
            }}
          />
        </Button>

        {preview && (
          <Box
            component="img"
            src={preview}
            sx={{ width: "100%", maxHeight: 450, objectFit: "contain" }}
          />
        )}
        {file && <Typography>{file.name}</Typography>}
        {error && <Typography color="error">{error}</Typography>}
      </Paper>

      <Paper sx={{ p: 3, width: 250 }}>
        <Typography variant="h6">Image Tools</Typography>
        <Select
          fullWidth
          value={size}
          onChange={(e) => setSize(Number(e.target.value))}
          sx={{ my: 2 }}
        >
          <MenuItem value={720}>720p</MenuItem>
          <MenuItem value={1080}>1080p</MenuItem>
          <MenuItem value={1440}>1440p</MenuItem>
        </Select>
        <Button
          fullWidth
          variant="contained"
          disabled={!file || loading}
          onClick={resize}
        >
          {loading ? <CircularProgress size={24} /> : "Resize Image"}
        </Button>
        {file && preview && !loading && (
          <Button fullWidth href={preview} download="image.png" sx={{ mt: 2 }}>
            Download Image
          </Button>
        )}
        {success && (
          <Typography color="success.main" sx={{ mt: 2 }}>
            ✓ Image processed successfully!
          </Typography>
        )}
      </Paper>
    </Box>
  );
};

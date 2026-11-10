const API_BASE_URL = "http://localhost:8080/api/image";

export const uploadImage = async (file: File): Promise<string> => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(API_BASE_URL, {
    method: "POST",
    body: formData,
  });

  if (!response.ok) {
    throw new Error(`Failed to upload image: ${response.status}`);
  }

  return response.text();
};

export const getImage = async (id: string): Promise<Blob> => {
  const response = await fetch(`${API_BASE_URL}/${encodeURIComponent(id)}`);

  if (!response.ok) {
    throw new Error(`Failed to fetch image: ${response.status}`);
  }

  return response.blob();
};

const JOBS_URL = "http://localhost:8080/api/jobs";
const sleep = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

export const processImage = async (id: string, size: number): Promise<string> => {

  const response = await fetch(
      `${API_BASE_URL}/process?imageId=${id}&operation=resize&width=${size}&height=${size}`,
      { method: "POST" },
  );
  if (!response.ok) throw new Error(`Processing failed: ${response.status}`);
  const jobId = (await response.text()).trim();

  for (let i = 0; i < 60; i++) {
    const job = await (await fetch(`${JOBS_URL}/${jobId}`)).json();
    if (job.status === "completed") return String(job.result_image_id);
    if (job.status === "failed") throw new Error(job.error ?? "Processing failed");
    await sleep(500);
  }
  throw new Error("Processing timed out");
};

export const deleteImage = async (id: string): Promise<void> => {
  const response = await fetch(`${API_BASE_URL}/${encodeURIComponent(id)}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    throw new Error(`Failed to delete image: ${response.status}`);
  }
};

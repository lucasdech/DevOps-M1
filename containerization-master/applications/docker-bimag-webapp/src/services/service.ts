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

export const processImage = async (
  id: string,
  size: number,
): Promise<string> => {
  const response = await fetch(
    `${API_BASE_URL}/${encodeURIComponent(id)}/process?size=${size}`,
    {
      method: "POST",
    },
  );

  if (!response.ok) {
    throw new Error(`Processing failed: ${response.status}`);
  }

  return response.text();
};

export const deleteImage = async (id: string): Promise<void> => {
  const response = await fetch(`${API_BASE_URL}/${encodeURIComponent(id)}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    throw new Error(`Failed to delete image: ${response.status}`);
  }
};

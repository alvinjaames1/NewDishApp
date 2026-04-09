(async () => {
  bindLogout();

  const username = await requireAuth({ redirectTo: "/login.html" });
  if (!username) return;

  const createDishForm = document.getElementById("createDishForm");
  const createDishMessage = document.getElementById("message");
  const uploadStatus = document.getElementById("uploadStatus");

  function setCreateDishMessage(text, type = "") {
    if (!createDishMessage) return;
    createDishMessage.className = type ? `message ${type}` : "message";
    createDishMessage.textContent = text;
  }

  function setUploadStatus(text, type = "") {
    if (!uploadStatus) return;
    uploadStatus.className = type ? `message ${type}` : "message";
    uploadStatus.textContent = text;
  }

  async function uploadImageIfNeeded() {
    const fileInput = document.getElementById("imageFile");
    const imageUrlInput = document.getElementById("imageUrl");

    const selectedFile = fileInput?.files?.[0];
    const typedUrl = imageUrlInput?.value?.trim() || "";

    if (selectedFile) {
      const formData = new FormData();
      formData.append("file", selectedFile);

      setUploadStatus("Uploading image...", "success");

      const result = await apiRequest("/dishes/upload-image", "POST", formData);
      setUploadStatus("Image uploaded successfully.", "success");
      return result.imageUrl;
    }

    if (typedUrl) {
      return typedUrl;
    }

    throw new Error("Please choose an image file or provide an image URL.");
  }

  if (createDishForm) {
    createDishForm.addEventListener("submit", async function (e) {
      e.preventDefault();

      setCreateDishMessage("", "");
      setUploadStatus("", "");

      try {
        const imageUrl = await uploadImageIfNeeded();

        const dishData = {
          title: document.getElementById("title").value.trim(),
          cuisine: document.getElementById("cuisine").value.trim(),
          description: document.getElementById("description").value.trim(),
          ingredientsText: document.getElementById("ingredientsText").value.trim(),
          recipeText: document.getElementById("recipeText").value.trim(),
          imageUrl: imageUrl
        };

        await apiRequest("/dishes", "POST", dishData);
        setCreateDishMessage("Dish created successfully! Redirecting...", "success");

        setTimeout(() => {
          window.location.href = "/feed.html";
        }, 800);
      } catch (error) {
        setCreateDishMessage(error.message || "Failed to create dish", "error");
      }
    });
  }
})();
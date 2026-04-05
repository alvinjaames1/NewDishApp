bindLogout();
requireAuth();

const createDishForm = document.getElementById("createDishForm");
const createDishMessage = document.getElementById("message");

function setCreateDishMessage(text, type = "") {
  if (!createDishMessage) return;
  createDishMessage.className = type ? `message ${type}` : "message";
  createDishMessage.textContent = text;
}

if (createDishForm) {
  createDishForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const dishData = {
      title: document.getElementById("title").value.trim(),
      cuisine: document.getElementById("cuisine").value.trim(),
      description: document.getElementById("description").value.trim(),
      ingredientsText: document.getElementById("ingredientsText").value.trim(),
      recipeText: document.getElementById("recipeText").value.trim(),
      imageUrl: document.getElementById("imageUrl").value.trim()
    };

    try {
      await apiRequest("/dishes", "POST", dishData);
      setCreateDishMessage("Dish created successfully! Redirecting...", "success");

      setTimeout(() => {
        window.location.href = "/feed.html";
      }, 1000);
    } catch (error) {
      console.error("Create dish error:", error);
      setCreateDishMessage(error.message || "Failed to create dish", "error");
    }
  });
}
bindLogout();

const params = new URLSearchParams(window.location.search);
const dishId = params.get("id");

const dishImage = document.getElementById("dishImage");
const dishTitle = document.getElementById("dishTitle");
const dishCuisine = document.getElementById("dishCuisine");
const dishDescription = document.getElementById("dishDescription");
const dishIngredients = document.getElementById("dishIngredients");
const dishRecipe = document.getElementById("dishRecipe");
const dishRating = document.getElementById("dishRating");
const dishLikes = document.getElementById("dishLikes");

const commentList = document.getElementById("commentList");
const interactionMessage = document.getElementById("interactionMessage");
const ratingForm = document.getElementById("ratingForm");
const commentForm = document.getElementById("commentForm");
const likeBtn = document.getElementById("likeBtn");
const commentMessage = document.getElementById("commentMessage");

function setInteractionMessage(text, type = "") {
  if (!interactionMessage) return;
  interactionMessage.className = type ? `message ${type}` : "message";
  interactionMessage.textContent = text;
}

function setCommentMessage(text, type = "") {
  if (!commentMessage) return;
  commentMessage.className = type ? `message ${type}` : "message";
  commentMessage.textContent = text;
}

async function loadDish() {
  if (!dishId) {
    if (dishTitle) dishTitle.textContent = "Dish not found";
    if (dishCuisine) dishCuisine.textContent = "-";
    if (dishDescription) dishDescription.textContent = "No dish ID was provided.";
    if (dishIngredients) dishIngredients.textContent = "-";
    if (dishRecipe) dishRecipe.textContent = "-";
    if (commentList) commentList.innerHTML = renderEmptyState("No comments found.");
    return;
  }

  try {
    const dish = await apiRequest(`/dishes/${dishId}`, "GET");

    if (dishTitle) dishTitle.textContent = dish.title || "Untitled Dish";
    if (dishCuisine) dishCuisine.textContent = dish.cuisine || "N/A";
    if (dishDescription) dishDescription.textContent = dish.description || "No description available.";
    if (dishIngredients) dishIngredients.textContent = dish.ingredientsText || "N/A";
    if (dishRecipe) dishRecipe.textContent = dish.recipeText || "N/A";
    if (dishRating) dishRating.textContent = `⭐ ${formatRating(dish.averageRating)}`;
    if (dishLikes) dishLikes.textContent = `Likes: ${dish.likeCount ?? 0}`;

    if (dishImage) {
      const finalImage = getDishImage(dish.imageUrl);
      dishImage.src = finalImage;
      dishImage.alt = dish.title || "Dish image";
      dishImage.onerror = function () {
        this.onerror = null;
        this.src = PLACEHOLDER_IMAGE;
      };
    }

    const comments = await apiRequest(`/posts/${dishId}/comments`, "GET");

    if (!commentList) return;

    if (!Array.isArray(comments) || !comments.length) {
      commentList.innerHTML = renderEmptyState("No comments yet.");
      return;
    }

    commentList.innerHTML = comments.map((comment) => `
      <div class="comment-item">
        <p><strong>${escapeHtml(comment.username || "User")}:</strong> ${escapeHtml(comment.text || "")}</p>
      </div>
    `).join("");
  } catch (error) {
    console.error("Dish details error:", error);

    if (dishTitle) dishTitle.textContent = "Unable to load dish";
    if (dishCuisine) dishCuisine.textContent = "-";
    if (dishDescription) dishDescription.textContent = error.message || "Failed to load dish details.";
    if (dishIngredients) dishIngredients.textContent = "-";
    if (dishRecipe) dishRecipe.textContent = "-";
    if (commentList) commentList.innerHTML = renderEmptyState("Unable to load comments.");
  }
}

if (likeBtn) {
  likeBtn.addEventListener("click", async function () {
    if (!requireAuth()) return;

    try {
      const response = await apiRequest(`/posts/${dishId}/likes`, "POST");
      setInteractionMessage(
          response?.liked ? "Post liked." : "Like removed.",
          "success"
      );
      await loadDish();
    } catch (error) {
      setInteractionMessage(error.message || "Like failed.", "error");
      console.error("Like error:", error);
    }
  });
}

if (ratingForm) {
  ratingForm.addEventListener("submit", async function (e) {
    e.preventDefault();
    if (!requireAuth()) return;

    const value = Number(document.getElementById("ratingValue").value);

    if (!Number.isInteger(value) || value < 1 || value > 5) {
      setInteractionMessage("Rating must be a whole number from 1 to 5.", "error");
      return;
    }

    try {
      await apiRequest(`/posts/${dishId}/ratings`, "POST", { value });
      setInteractionMessage("Rating submitted.", "success");
      ratingForm.reset();
      await loadDish();
    } catch (error) {
      setInteractionMessage(error.message || "Rating failed.", "error");
      console.error("Rating error:", error);
    }
  });
}

if (commentForm) {
  commentForm.addEventListener("submit", async function (e) {
    e.preventDefault();
    if (!requireAuth()) return;

    const text = document.getElementById("commentText").value.trim();

    if (!text) {
      setCommentMessage("Comment cannot be empty.", "error");
      return;
    }

    try {
      await apiRequest(`/posts/${dishId}/comments`, "POST", { text });
      setCommentMessage("Comment posted.", "success");
      commentForm.reset();
      await loadDish();
    } catch (error) {
      setCommentMessage(error.message || "Comment failed.", "error");
      console.error("Comment error:", error);
    }
  });
}

loadDish();
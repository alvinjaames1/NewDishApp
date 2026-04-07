bindLogout();

const params = new URLSearchParams(window.location.search);
const dishId = params.get("id");

const dishDetails = document.getElementById("dishDetails");
const commentList = document.getElementById("commentList");
const interactionMessage = document.getElementById("interactionMessage");
const ratingForm = document.getElementById("ratingForm");
const commentForm = document.getElementById("commentForm");
const likeBtn = document.getElementById("likeBtn");

function setInteractionMessage(text, type = "") {
  if (!interactionMessage) return;
  interactionMessage.className = type ? `message ${type}` : "message";
  interactionMessage.textContent = text;
}

async function loadDish() {
  if (!dishId) {
    dishDetails.innerHTML = renderEmptyState("No dish ID was provided.");
    commentList.innerHTML = "";
    return;
  }

  dishDetails.innerHTML = renderEmptyState("Loading dish...");
  commentList.innerHTML = renderEmptyState("Loading comments...");

  try {
    const dish = await apiRequest(`/dishes/${dishId}`, "GET");

    dishDetails.innerHTML = `
      <div class="dish-detail-layout">
        <img
          class="dish-detail-image"
          src="${escapeHtml(getDishImage(dish.imageUrl))}"
          alt="${escapeHtml(dish.title || "Dish image")}"
          onerror="this.src='${PLACEHOLDER_IMAGE}';"
        />

        <div class="dish-detail-text">
          <h2>${escapeHtml(dish.title || "Untitled Dish")}</h2>
          <p><strong>Cuisine:</strong> ${escapeHtml(dish.cuisine || "N/A")}</p>
          <p><strong>Description:</strong> ${escapeHtml(dish.description || "No description available.")}</p>
          <p><strong>Ingredients:</strong> ${escapeHtml(dish.ingredientsText || "N/A")}</p>
          <p><strong>Recipe:</strong> ${escapeHtml(dish.recipeText || "N/A")}</p>

          <div class="dish-meta">
            <span class="rating-badge">⭐ ${escapeHtml(formatRating(dish.averageRating))}</span>
            <span class="tag">Likes: ${escapeHtml(dish.likeCount ?? 0)}</span>
          </div>
        </div>
      </div>
    `;

    const comments = await apiRequest(`/posts/${dishId}/comments`, "GET");

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
    dishDetails.innerHTML = renderEmptyState(error.message || "Unable to load dish details.");
    commentList.innerHTML = "";
    console.error("Dish details error:", error);
  }
}

if (likeBtn) {
  likeBtn.addEventListener("click", async function () {
    if (!await requireAuth()) return;         // ✅ FIX 1

    try {
      const response = await apiRequest(`/posts/${dishId}/likes`, "POST");
      setInteractionMessage(
        response?.liked ? "Post liked." : "Like removed.",
        "success"
      );
      loadDish();
    } catch (error) {
      setInteractionMessage(error.message || "Like failed.", "error");
      console.error("Like error:", error);
    }
  });
}

if (ratingForm) {
  ratingForm.addEventListener("submit", async function (e) {
    e.preventDefault();
    if (!await requireAuth()) return;         // ✅ FIX 2

    const value = Number(document.getElementById("ratingValue").value);

    if (!Number.isInteger(value) || value < 1 || value > 5) {
      setInteractionMessage("Rating must be a whole number from 1 to 5.", "error");
      return;
    }

    try {
      await apiRequest(`/posts/${dishId}/ratings`, "POST", { value });
      setInteractionMessage("Rating submitted.", "success");
      ratingForm.reset();
      loadDish();
    } catch (error) {
      setInteractionMessage(error.message || "Rating failed.", "error");
      console.error("Rating error:", error);
    }
  });
}

if (commentForm) {
  commentForm.addEventListener("submit", async function (e) {
    e.preventDefault();
    if (!await requireAuth()) return;         // ✅ FIX 3

    const text = document.getElementById("commentText").value.trim();

    if (!text) {
      setInteractionMessage("Comment cannot be empty.", "error");
      return;
    }

    try {
      await apiRequest(`/posts/${dishId}/comments`, "POST", { text });
      setInteractionMessage("Comment posted.", "success");
      commentForm.reset();
      loadDish();
    } catch (error) {
      setInteractionMessage(error.message || "Comment failed.", "error");
      console.error("Comment error:", error);
    }
  });
}

loadDish();
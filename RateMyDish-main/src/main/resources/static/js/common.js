const PLACEHOLDER_IMAGE =
    "data:image/svg+xml;charset=UTF-8," +
    encodeURIComponent(`
    <svg xmlns="http://www.w3.org/2000/svg" width="400" height="250" viewBox="0 0 400 250">
      <rect width="400" height="250" fill="#f5d8b6"/>
      <text x="200" y="125" text-anchor="middle" dominant-baseline="middle"
            font-family="Arial, sans-serif" font-size="24" fill="#8a6b4f">
        No Image
      </text>
    </svg>
  `);

function escapeHtml(value) {
  return String(value ?? "")
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#39;");
}

function getDishImage(imageUrl) {
  const value = String(imageUrl ?? "").trim();

  if (!value) return PLACEHOLDER_IMAGE;

  if (
      value.startsWith("/uploads/") ||
      value.startsWith("http://") ||
      value.startsWith("https://") ||
      value.startsWith("data:image/")
  ) {
    return value;
  }

  return PLACEHOLDER_IMAGE;
}

function formatRating(value) {
  const num = Number(value);
  return Number.isFinite(num) ? num.toFixed(1) : "0.0";
}

function renderEmptyState(message) {
  return `<p class="empty-state">${escapeHtml(message)}</p>`;
}

function requireAuth() {
  const username = localStorage.getItem("username");
  if (!username) {
    window.location.href = "/login.html";
    return false;
  }
  return true;
}

function bindLogout() {
  const logoutBtn = document.getElementById("logoutBtn");
  if (!logoutBtn) return;

  logoutBtn.addEventListener("click", async function (e) {
    e.preventDefault();

    try {
      if (typeof apiRequest === "function") {
        await apiRequest("/auth/logout", "POST");
      }
    } catch (error) {
      console.error("Logout error:", error);
    }

    localStorage.removeItem("username");
    window.location.href = "/login.html";
  });
}

function createDishCardMarkup(dish) {
  const imageSrc = getDishImage(dish.imageUrl);

  return `
    <img
      src="${escapeHtml(imageSrc)}"
      alt="${escapeHtml(dish.title || "Dish")}"
      onerror="this.onerror=null; this.src='${PLACEHOLDER_IMAGE}';"
    />
    <div class="dish-card-content">
      <h3>${escapeHtml(dish.title || "Untitled Dish")}</h3>
      <p><strong>Cuisine:</strong> ${escapeHtml(dish.cuisine || "N/A")}</p>
      <p>${escapeHtml(dish.description || "No description available.")}</p>
      <div class="dish-meta">
        <span class="rating-badge">⭐ ${escapeHtml(formatRating(dish.averageRating))}</span>
        <span class="tag">Likes: ${escapeHtml(dish.likeCount ?? 0)}</span>
      </div>
      <div class="actions" style="margin-top: 1rem;">
        <a class="btn" href="/dish-details.html?id=${dish.id}">View Details</a>
      </div>
    </div>
  `;
}
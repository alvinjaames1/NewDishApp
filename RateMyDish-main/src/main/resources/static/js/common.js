
const PLACEHOLDER_IMAGE = "https://via.placeholder.com/400x250?text=No+Image";

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function getDishImage(imageUrl) {
  const trimmed = String(imageUrl ?? "").trim();
  return trimmed ? trimmed : PLACEHOLDER_IMAGE;
}

function formatRating(value) {
  const num = Number(value);
  return Number.isFinite(num) ? num.toFixed(1) : "0.0";
}

function renderEmptyState(message) {
  return `<div class="empty-state"><p>${escapeHtml(message)}</p></div>`;
}

function createDishCardMarkup(dish) {
  const title = dish?.title || "Untitled Dish";
  const cuisine = dish?.cuisine || "N/A";
  const description = dish?.description || "No description available.";
  const avg = formatRating(dish?.averageRating);
  const likes = dish?.likeCount ?? 0;
  const id = dish?.id;

  return `
    <img
      src="${escapeHtml(getDishImage(dish?.imageUrl))}"
      alt="${escapeHtml(title)}"
      onerror="this.src='${PLACEHOLDER_IMAGE}';"
    />
    <div class="dish-card-content">
      <h3>${escapeHtml(title)}</h3>
      <p><strong>Cuisine:</strong> ${escapeHtml(cuisine)}</p>
      <p>${escapeHtml(description)}</p>
      <div class="dish-meta">
        <span class="rating-badge">⭐ ${escapeHtml(avg)}</span>
        <span class="tag">Likes: ${escapeHtml(likes)}</span>
      </div>
      <div class="actions" style="margin-top: 1rem;">
        ${id ? `<a class="btn" href="/dish-details.html?id=${id}">View Details</a>` : ""}
      </div>
    </div>
  `;
}

/**
 * Calls /api/auth/me and returns username (string) or null.
 * This is the canonical session check.
 */
async function fetchSessionUser() {
  try {
    const me = await apiRequest("/auth/me", "GET");
    const username = me?.username ? String(me.username) : null;
    if (username) {
      setUsername(username); // UI convenience
      return username;
    }
  } catch (err) {
    // Not authenticated or other error.
  }
  clearAuthState();
  return null;
}

/**
 * Enforces that a user is logged in (session-based).
 * Returns username string if authenticated, otherwise redirects (default) and returns null.
 */
async function requireAuth(options = {}) {
  const { redirectTo = "/login.html", redirect = true } = options;

  const username = await fetchSessionUser();
  if (username) return username;

  if (redirect) {
    window.location.href = redirectTo;
  }
  return null;
}

function bindLogout() {
  const logoutBtn = document.getElementById("logoutBtn");
  if (!logoutBtn) return;

  logoutBtn.addEventListener("click", async (e) => {
    e.preventDefault();

    try {
      // Backend endpoint invalidates session
      await apiRequest("/auth/logout", "POST");
    } catch (err) {
      // Even if the server says we're already logged out, we still clear client UI state.
    } finally {
      clearAuthState();
      localStorage.removeItem("token"); // legacy cleanup, harmless
      window.location.href = "/login.html";
    }
  });
}

const dishList = document.getElementById("dishList");
const logoutBtnFeed = document.getElementById("logoutBtn");

if (logoutBtnFeed) {
  logoutBtnFeed.addEventListener("click", async function (e) {
    e.preventDefault();
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    window.location.href = "/login.html";
  });
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function getDishImage(imageUrl) {
  return imageUrl && String(imageUrl).trim()
    ? String(imageUrl).trim()
    : "https://via.placeholder.com/400x250?text=No+Image";
}

async function loadDishes() {
  if (!dishList) return;

  dishList.innerHTML = "<p>Loading dishes...</p>";

  try {
    const response = await apiRequest("/dishes?sort=latest", "GET");
    const dishes = Array.isArray(response) ? response : (response?.content || []);

    dishList.innerHTML = "";

    if (!dishes.length) {
      dishList.innerHTML = "<p>No dishes found.</p>";
      return;
    }

    dishes.forEach((dish) => {
      const card = document.createElement("div");
      card.className = "dish-card";

      card.innerHTML = `
        <img
          src="${escapeHtml(getDishImage(dish.imageUrl))}"
          alt="${escapeHtml(dish.title || "Dish")}"
          onerror="this.src='https://via.placeholder.com/400x250?text=No+Image';"
        />
        <div class="dish-card-content">
          <h3>${escapeHtml(dish.title || "Untitled Dish")}</h3>
          <p><strong>Cuisine:</strong> ${escapeHtml(dish.cuisine || "N/A")}</p>
          <p>${escapeHtml(dish.description || "No description available.")}</p>
          <div class="dish-meta">
            <span class="rating-badge">⭐ ${dish.averageRating ?? 0}</span>
            <span class="tag">Likes: ${dish.likeCount ?? 0}</span>
          </div>
          <div class="actions" style="margin-top: 1rem;">
            <a class="btn" href="/dish-details.html?id=${dish.id}">View Details</a>
          </div>
        </div>
      `;

      dishList.appendChild(card);
    });
  } catch (error) {
    dishList.innerHTML = `<p>${escapeHtml(error.message || "Failed to load feed.")}</p>`;
    console.error("Feed error:", error);
  }
}

loadDishes();
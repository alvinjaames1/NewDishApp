bindLogout();
requireAuth();

const profileInfo = document.getElementById("profileInfo");
const myDishList = document.getElementById("myDishList");

async function loadProfile() {
  if (!profileInfo || !myDishList) return;

  const username = getUsername();

  if (!username) {
    profileInfo.innerHTML = renderEmptyState("No logged-in user found.");
    myDishList.innerHTML = "";
    return;
  }

  profileInfo.innerHTML = `
    <div class="profile-summary">
      <p><strong>Username:</strong> ${escapeHtml(username)}</p>
    </div>
  `;

  myDishList.innerHTML = renderEmptyState("Loading your dishes...");

  try {
    const response = await apiRequest(`/dishes/user/${encodeURIComponent(username)}`, "GET");
    const dishes = Array.isArray(response) ? response : (response?.content || []);

    if (!dishes.length) {
      myDishList.innerHTML = renderEmptyState("No dishes posted yet.");
      return;
    }

    myDishList.innerHTML = "";

    dishes.forEach((dish) => {
      const card = document.createElement("article");
      card.className = "dish-card";
      card.innerHTML = createDishCardMarkup(dish);
      myDishList.appendChild(card);
    });
  } catch (error) {
    myDishList.innerHTML = renderEmptyState(error.message || "Unable to load your dishes.");
    console.error("Profile error:", error);
  }
}

loadProfile();
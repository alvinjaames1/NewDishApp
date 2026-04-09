bindLogout();

const profileUsername = localStorage.getItem("username");

const profileName = document.getElementById("profileName");
const profileDishList = document.getElementById("profileDishList");

async function loadProfile() {
  if (!requireAuth()) return;

  if (profileName) {
    profileName.textContent = profileUsername || "User";
  }

  if (!profileUsername) {
    if (profileDishList) {
      profileDishList.innerHTML = renderEmptyState("No username found. Please log in again.");
    }
    return;
  }

  try {
    const response = await apiRequest(`/dishes/user/${encodeURIComponent(profileUsername)}`, "GET");
    const dishes = Array.isArray(response) ? response : (response?.content || []);

    if (!Array.isArray(dishes) || dishes.length === 0) {
      profileDishList.innerHTML = renderEmptyState("You have not posted any dishes yet.");
      return;
    }

    profileDishList.innerHTML = dishes.map((dish) => `
      <div class="dish-card">
        ${createDishCardMarkup(dish)}
      </div>
    `).join("");
  } catch (error) {
    console.error("Profile load error:", error);
    profileDishList.innerHTML = renderEmptyState(error.message || "Unable to load your dishes.");
  }
}

loadProfile();
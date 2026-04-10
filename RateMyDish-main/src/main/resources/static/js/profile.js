bindLogout();

const profileUsername = localStorage.getItem("username");

const profileName = document.getElementById("profileName");
const profileDishList = document.getElementById("profileDishList");

async function deleteDish(dishId) {
  const confirmed = window.confirm("Delete this dish?");
  if (!confirmed) return;

  try {
    await apiRequest(`/dishes/${dishId}`, "DELETE");
    await loadProfile();
  } catch (error) {
    console.error("Delete error:", error);
    alert(error.message || "Failed to delete dish.");
  }
}

function renderProfileDishCard(dish) {
  return `
    <div class="dish-card">
      ${createDishCardMarkup(dish)}
      <div class="actions" style="padding: 0 1.15rem 1.2rem;">
        <button class="btn delete-btn" type="button" onclick="deleteDish(${dish.id})">
          Delete
        </button>
      </div>
    </div>
  `;
}

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

    profileDishList.innerHTML = dishes.map(renderProfileDishCard).join("");
  } catch (error) {
    console.error("Profile load error:", error);
    profileDishList.innerHTML = renderEmptyState(error.message || "Unable to load your dishes.");
  }
}

window.deleteDish = deleteDish;

loadProfile();
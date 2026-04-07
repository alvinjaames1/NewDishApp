(async () => {
  bindLogout();

  const username = await requireAuth({ redirectTo: "/login.html" });
  if (!username) return;

  const profileInfo = document.getElementById("profileInfo");
  const myDishList = document.getElementById("myDishList");

  async function loadProfile() {
    if (!profileInfo || !myDishList) return;

    profileInfo.innerHTML = `
      <div class="profile-header">
        <h2>${escapeHtml(username)}</h2>
        <p class="muted">Your dishes</p>
      </div>
    `;

    myDishList.innerHTML = renderEmptyState("Loading your dishes...");

    try {
      const dishes = await apiRequest(`/dishes/user/${encodeURIComponent(username)}`, "GET");

      if (!Array.isArray(dishes) || dishes.length === 0) {
        myDishList.innerHTML = renderEmptyState("You have not posted any dishes yet.");
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
    }
  }

  loadProfile();
})();

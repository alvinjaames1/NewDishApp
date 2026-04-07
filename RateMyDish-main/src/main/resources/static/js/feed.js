bindLogout();

const dishList = document.getElementById("dishList");

async function loadDishes() {
  if (!dishList) return;

  dishList.innerHTML = renderEmptyState("Loading dishes...");

  try {
    const response = await apiRequest("/dishes?feedSort=latest", "GET");
    const dishes = Array.isArray(response) ? response : (response?.content || []);

    if (!dishes.length) {
      dishList.innerHTML = renderEmptyState("No dishes yet.");
      return;
    }

    dishList.innerHTML = "";
    dishes.forEach((dish) => {
      const card = document.createElement("article");
      card.className = "dish-card";
      card.innerHTML = createDishCardMarkup(dish);
      dishList.appendChild(card);
    });
  } catch (error) {
    dishList.innerHTML = renderEmptyState(error.message || "Failed to load feed.");
  }
}

loadDishes();

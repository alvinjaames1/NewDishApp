bindLogout();

const searchForm = document.getElementById("searchForm");
const dishResults = document.getElementById("dishResults");
const searchMessage = document.getElementById("searchMessage");

if (searchForm) {
  searchForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const keyword = document.getElementById("keyword").value.trim();
    const cuisine = document.getElementById("cuisineFilter").value.trim();

    if (!keyword) {
      searchMessage.className = "message error";
      searchMessage.textContent = "Please enter a keyword.";
      dishResults.innerHTML = renderEmptyState("Enter a search term to continue.");
      return;
    }

    searchMessage.className = "message";
    searchMessage.textContent = "";
    dishResults.innerHTML = renderEmptyState("Searching...");

    try {
      const query = new URLSearchParams();
      query.set("keyword", keyword);
      if (cuisine) query.set("cuisine", cuisine);

      const response = await apiRequest(`/search/dishes?${query.toString()}`, "GET");
      const dishes = Array.isArray(response) ? response : (response?.content || []);

      if (!dishes.length) {
        dishResults.innerHTML = renderEmptyState("No dishes found.");
        return;
      }

      dishResults.innerHTML = "";

      dishes.forEach((dish) => {
        const card = document.createElement("article");
        card.className = "dish-card";
        card.innerHTML = createDishCardMarkup(dish);
        dishResults.appendChild(card);
      });
    } catch (error) {
      searchMessage.className = "message error";
      searchMessage.textContent = error.message || "Search failed.";
      dishResults.innerHTML = renderEmptyState("Could not load search results.");
      console.error("Search error:", error);
    }
  });
}
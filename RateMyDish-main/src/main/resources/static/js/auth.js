const registerForm = document.getElementById("registerForm");
const loginForm = document.getElementById("loginForm");
const messageEl = document.getElementById("message");

if (registerForm) {
  registerForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const userData = {
      username: document.getElementById("username").value.trim(),
      email: document.getElementById("email").value.trim(),
      password: document.getElementById("password").value.trim()
    };

    try {
      const response = await apiRequest("/auth/register", "POST", userData);
      messageEl.textContent = response.message || "Registration successful";

      setTimeout(() => {
        window.location.href = "/login.html";
      }, 1000);
    } catch (error) {
      messageEl.textContent = error.message;
      console.error("Register error:", error);
    }
  });
}

if (loginForm) {
  loginForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const username = document.getElementById("loginUsername").value.trim();
    const password = document.getElementById("loginPassword").value.trim();

    try {
      const response = await apiRequest("/auth/login", "POST", {
        username,
        password
      });

      setUsername(response.username || username);
      messageEl.textContent = response.message || "Login successful";

      setTimeout(() => {
        window.location.href = "/feed.html";
      }, 800);
    } catch (error) {
      clearAuthState();
      messageEl.textContent = error.message || "Login failed";
      console.error("Login error:", error);
    }
  });
}
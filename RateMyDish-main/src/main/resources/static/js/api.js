const API_BASE = "/api";

async function apiRequest(endpoint, method = "GET", body = null) {
  const options = {
    method,
    credentials: "same-origin",
    headers: {}
  };

  if (body !== null) {
    if (body instanceof FormData) {
      options.body = body;
    } else {
      options.headers["Content-Type"] = "application/json";
      options.body = JSON.stringify(body);
    }
  }

  const response = await fetch(`${API_BASE}${endpoint}`, options);

  const contentType = response.headers.get("content-type") || "";
  let data = null;

  if (contentType.includes("application/json")) {
    data = await response.json();
  } else {
    const text = await response.text();
    data = text ? { message: text } : null;
  }

  if (!response.ok) {
    throw new Error(data?.message || "Request failed");
  }

  return data;
}

function getUsername() {
  return localStorage.getItem("username");
}

function setUsername(username) {
  if (username) {
    localStorage.setItem("username", username);
  }
}

function clearAuthState() {
  localStorage.removeItem("username");
}
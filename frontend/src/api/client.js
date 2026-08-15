import axios from "axios";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
  timeout: 10000,
});

// Centralizing every endpoint call here means components never construct
// URLs themselves — if a route changes, this is the only file that changes.

export async function fetchSystems() {
  const { data } = await apiClient.get("/api/systems");
  return data;
}

export async function fetchSystemImpact(systemId) {
  const { data } = await apiClient.get(`/api/impact/${systemId}`);
  return data;
}

export async function fetchShortestPath(fromId, toId) {
  const { data } = await apiClient.get("/api/analytics/shortest-path", {
    params: { from: fromId, to: toId },
  });
  return data;
}

export async function fetchPiiTrace(sensitivity = "PII") {
  const { data } = await apiClient.get("/api/analytics/pii-trace", {
    params: { sensitivity },
  });
  return data;
}

export async function fetchSpofRanking() {
  const { data } = await apiClient.get("/api/analytics/spof-ranking");
  return data;
}

export default apiClient;
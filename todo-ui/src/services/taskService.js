const API_URL = "http://localhost:8080/tasks";

const getHeaders = (token) => ({
  "Content-Type": "application/json",
  "Authorization": `Bearer ${token}`
});

export async function login(username, password) {

  const response = await fetch("http://localhost:8080/security/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Usuário ou senha inválidos");
  }

  const data = await response.json();

  return data.token;
}

export async function register(username, email, password) {
  const response = await fetch("http://localhost:8080/security/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: username,
      email: email,
      password: password,
      role: "USER"
    }),
  });
  if (!response.ok) throw new Error("Erro ao cadastrar usuário");
  return true;
}

export async function getTasks(token) {
  const response = await fetch(API_URL, {
    method: "GET",
    headers: getHeaders(token)
  });

  if (!response.ok) {
    console.error("Erro ao buscar tasks:", response.status);
    return [];
  }

  const data = await response.json();

  return data.content ?? [];
}

export async function deleteTask(id, token) {
  const response = await fetch(`${API_URL}/${id}`, {
    method: "DELETE",
    headers: getHeaders(token)
  });

  if (!response.ok) {
    console.error("Erro ao deletar task:", response.status);
  }
}

export async function updateTask(task, token) {
  const response = await fetch(`${API_URL}/${task.id}`, {
    method: "PUT",
    headers: getHeaders(token),
    body: JSON.stringify(task)
  });

  if (!response.ok) {
    throw new Error("Erro ao atualizar task");
  }

  return response.json();
}

export async function createTask(task, token) {
  const response = await fetch(API_URL, {
    method: "POST",
    headers: getHeaders(token),
    body: JSON.stringify(task)
  });

  if (!response.ok) {
    throw new Error("Erro ao criar task");
  }

  return response.json();
}
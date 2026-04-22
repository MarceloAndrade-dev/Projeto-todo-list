import { useState, useEffect } from "react";
import { getTasks, createTask, deleteTask, updateTask, login, register } from "./services/taskService";

function App() {
  const [tasks, setTasks] = useState([]);
  const [taskTitle, setTaskTitle] = useState("");
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState(""); // Novo estado para o e-mail
  const [password, setPassword] = useState("");
  const [isRegistering, setIsRegistering] = useState(false);

  // --- FUNÇÕES DE APOIO ---

  function handleLogout() {
    localStorage.removeItem("token");
    setToken(null);
    setTasks([]);
  }

  async function loadTasks() {
    try {
      const data = await getTasks(token);
      setTasks(data);
    } catch (e) {
      if (e.message && e.message.includes("403")) handleLogout();
    }
  }

  // --- HOOKS ---

  useEffect(() => {
    if (token) loadTasks();
  }, [token]);

  // --- HANDLERS ---

  async function handleLogin(e) {
    e.preventDefault();
    try {
      const receivedToken = await login(username, password);
      localStorage.setItem("token", receivedToken);
      setToken(receivedToken);
    } catch (e) {
      alert("Erro ao entrar: " + e.message);
    }
  }

  async function handleRegister(e) {
    e.preventDefault();
    try {
      // Passando o e-mail para a função de serviço
      await register(username, email, password);
      alert("Cadastro realizado com sucesso!");
      setIsRegistering(false);
    } catch (e) {
      alert("Erro ao cadastrar: " + e.message);
    }
  }

  async function handleAddTask() {
    if (!taskTitle.trim()) return;
    try {
      const created = await createTask({ title: taskTitle, status: "PENDING" }, token);
      setTasks([...tasks, created]);
      setTaskTitle("");
    } catch (e) {
      alert("Erro ao adicionar tarefa.");
    }
  }

  async function handleToggleStatus(task) {
    const newStatus = task.status === "PENDING" ? "DONE" : "PENDING";
    try {
      const updated = await updateTask({ ...task, status: newStatus }, token);
      setTasks(tasks.map(t => t.id === updated.id ? updated : t));
    } catch (e) {
      alert("Erro ao atualizar status.");
    }
  }

  // --- ESTILOS ---
  const containerStyle = { display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", minHeight: "100vh", backgroundColor: "#f4f7f6", fontFamily: "'Segoe UI', sans-serif" };
  const cardStyle = { backgroundColor: "white", padding: "30px", borderRadius: "12px", boxShadow: "0 10px 25px rgba(0,0,0,0.1)", width: "100%", maxWidth: "400px", textAlign: "center" };
  const inputStyle = { padding: "12px", borderRadius: "6px", border: "1px solid #ddd" };
  const linkStyle = { color: "#2196F3", cursor: "pointer", fontSize: "14px", marginTop: "15px", textDecoration: "underline", display: "inline-block" };

  // --- RENDERIZAÇÃO ---

  if (!token) {
    return (
        <div style={containerStyle}>
          <div style={cardStyle}>
            <h2 style={{ color: "#333", marginBottom: "20px" }}>
              {isRegistering ? "Crie sua conta" : "Bem-vindo de volta!"}
            </h2>
            <form onSubmit={isRegistering ? handleRegister : handleLogin} style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
              <input
                  style={inputStyle}
                  type="text" placeholder="Usuário"
                  onChange={e => setUsername(e.target.value)}
                  required
              />

              {/* Campo de e-mail visível apenas no Registro */}
              {isRegistering && (
                  <input
                      style={inputStyle}
                      type="email" placeholder="E-mail"
                      onChange={e => setEmail(e.target.value)}
                      required
                  />
              )}

              <input
                  style={inputStyle}
                  type="password" placeholder="Senha"
                  onChange={e => setPassword(e.target.value)}
                  required
              />

              <button type="submit" style={{
                padding: "12px",
                backgroundColor: isRegistering ? "#2196F3" : "#4CAF50",
                color: "white", border: "none", borderRadius: "6px", cursor: "pointer", fontWeight: "bold"
              }}>
                {isRegistering ? "Finalizar Cadastro" : "Entrar"}
              </button>
            </form>

            <p onClick={() => setIsRegistering(!isRegistering)} style={linkStyle}>
              {isRegistering ? "Já possui uma conta? Entre aqui" : "Não tem conta? Cadastre-se agora"}
            </p>
          </div>
        </div>
    );
  }

  return (
      <div style={containerStyle}>
        <div style={cardStyle}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" }}>
            <h2 style={{ margin: 0, color: "#333" }}>📝 Minhas Tarefas</h2>
            <button onClick={handleLogout} style={{ background: "none", border: "none", color: "#ff4d4d", cursor: "pointer", fontWeight: "bold" }}>Sair</button>
          </div>

          <div style={{ display: "flex", gap: "5px", marginBottom: "20px" }}>
            <input
                style={{ flex: 1, padding: "10px", borderRadius: "6px", border: "1px solid #ddd" }}
                value={taskTitle}
                onChange={(e) => setTaskTitle(e.target.value)}
                placeholder="O que precisa ser feito?"
            />
            <button onClick={handleAddTask} style={{ padding: "10px 15px", backgroundColor: "#2196F3", color: "white", border: "none", borderRadius: "6px", cursor: "pointer" }}>
              +
            </button>
          </div>

          <ul style={{ padding: 0, margin: 0 }}>
            {tasks.map((t) => (
                <li key={t.id} style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "10px", borderBottom: "1px solid #eee", gap: "10px" }}>
                  <div style={{ display: "flex", alignItems: "center", gap: "10px", cursor: "pointer" }} onClick={() => handleToggleStatus(t)}>
                    <div style={{
                      width: "20px", height: "20px", borderRadius: "50%", border: "2px solid #4CAF50",
                      backgroundColor: t.status === "DONE" ? "#4CAF50" : "transparent",
                      display: "flex", alignItems: "center", justifyContent: "center", color: "white", fontSize: "12px"
                    }}>
                      {t.status === "DONE" && "✓"}
                    </div>
                    <span style={{ textDecoration: t.status === "DONE" ? "line-through" : "none", color: t.status === "DONE" ? "#aaa" : "#333" }}>
                  {t.title}
                </span>
                  </div>
                  <button onClick={() => deleteTask(t.id, token).then(loadTasks)} style={{ background: "none", border: "none", cursor: "pointer", fontSize: "16px" }}>
                    🗑️
                  </button>
                </li>
            ))}
          </ul>
        </div>
      </div>
  );
}

export default App;
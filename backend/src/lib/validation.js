function normalizeEmail(email) {
  return String(email || "").trim().toLowerCase();
}

function validateEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function validateCadastro(body) {
  const nome = String(body.nome || "").trim();
  const email = normalizeEmail(body.email);
  const senha = String(body.senha || body.password || "");

  if (nome.length < 2) {
    return { ok: false, message: "Nome precisa ter pelo menos 2 caracteres." };
  }

  if (!validateEmail(email)) {
    return { ok: false, message: "Email invalido." };
  }

  if (senha.length < 8) {
    return { ok: false, message: "Senha precisa ter pelo menos 8 caracteres." };
  }

  return { ok: true, data: { nome, email, senha } };
}

function validateLogin(body) {
  const email = normalizeEmail(body.email);
  const senha = String(body.senha || body.password || "");

  if (!validateEmail(email)) {
    return { ok: false, message: "Email invalido." };
  }

  if (!senha) {
    return { ok: false, message: "Senha obrigatoria." };
  }

  return { ok: true, data: { email, senha } };
}

module.exports = {
  validateCadastro,
  validateLogin,
};

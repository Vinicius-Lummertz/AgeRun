const { supabase } = require("../lib/supabase");

async function requireAuth(req, res, next) {
  const authHeader = req.headers.authorization || "";
  const token = authHeader.startsWith("Bearer ") ? authHeader.slice("Bearer ".length).trim() : "";

  if (!token) {
    return res.status(401).json({ error: "Token de autenticacao obrigatorio." });
  }

  const { data: userData, error: userError } = await supabase.auth.getUser(token);

  if (userError || !userData.user) {
    return res.status(401).json({ error: "Sessao invalida ou expirada." });
  }

  const { data: profile, error: profileError } = await supabase
    .from("profiles")
    .select("id, nome, email, role")
    .eq("id", userData.user.id)
    .single();

  if (profileError || !profile) {
    return res.status(401).json({ error: "Perfil nao encontrado." });
  }

  req.auth = {
    token,
    user: userData.user,
    profile,
  };

  return next();
}

function requireProfessor(req, res, next) {
  if (req.auth?.profile?.role !== "professor") {
    return res.status(403).json({ error: "Apenas professores podem executar esta acao." });
  }

  return next();
}

module.exports = {
  requireAuth,
  requireProfessor,
};

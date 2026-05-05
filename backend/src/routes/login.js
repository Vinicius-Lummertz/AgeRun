const express = require("express");
const { supabase } = require("../lib/supabase");
const { validateLogin } = require("../lib/validation");

const router = express.Router();

router.post("/", async (req, res) => {
  const validation = validateLogin(req.body);

  if (!validation.ok) {
    return res.status(400).json({ error: validation.message });
  }

  const { email, senha } = validation.data;

  const { data: sessionData, error: loginError } = await supabase.auth.signInWithPassword({
    email,
    password: senha,
  });

  if (loginError || !sessionData.user || !sessionData.session) {
    return res.status(401).json({ error: "Email ou senha invalidos." });
  }

  const { data: profile, error: profileError } = await supabase
    .from("profiles")
    .select("id, nome, email, role")
    .eq("id", sessionData.user.id)
    .single();

  if (profileError) {
    return res.status(500).json({ error: "Login feito, mas nao foi possivel carregar o perfil." });
  }

  return res.json({
    message: "Login feito com sucesso.",
    user: profile,
    session: {
      access_token: sessionData.session.access_token,
      refresh_token: sessionData.session.refresh_token,
      expires_at: sessionData.session.expires_at,
      token_type: sessionData.session.token_type,
    },
  });
});

module.exports = router;

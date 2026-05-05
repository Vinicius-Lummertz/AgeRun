const express = require("express");
const { supabase } = require("../lib/supabase");
const { validateCadastro } = require("../lib/validation");

const router = express.Router();

router.post("/", async (req, res) => {
  const validation = validateCadastro(req.body);

  if (!validation.ok) {
    return res.status(400).json({ error: validation.message });
  }

  const { nome, email, senha } = validation.data;

  const { data: existingProfile, error: existingProfileError } = await supabase
    .from("profiles")
    .select("id")
    .eq("email", email)
    .maybeSingle();

  if (existingProfileError) {
    return res.status(500).json({ error: "Nao foi possivel validar o cadastro agora." });
  }

  if (existingProfile) {
    return res.status(409).json({ error: "Ja existe um usuario com esse email." });
  }

  const { data: authData, error: authError } = await supabase.auth.admin.createUser({
    email,
    password: senha,
    email_confirm: true,
    user_metadata: { nome },
  });

  if (authError) {
    return res.status(400).json({ error: authError.message });
  }

  const user = authData.user;

  const { data: profile, error: profileError } = await supabase
    .from("profiles")
    .upsert(
      {
        id: user.id,
        nome,
        email,
        role: "aluno",
      },
      { onConflict: "id" },
    )
    .select("id, nome, email, role, created_at")
    .single();

  if (profileError) {
    await supabase.auth.admin.deleteUser(user.id);
    return res.status(500).json({ error: "Usuario criado no Auth, mas falhou ao criar o perfil." });
  }

  return res.status(201).json({
    message: "Cadastro criado com sucesso.",
    user: profile,
  });
});

module.exports = router;
const express = require("express");
const { requireAuth, requireProfessor } = require("../middleware/auth");
const { supabase } = require("../lib/supabase");

const router = express.Router();

function validateRecado(body, partial = false) {
  const titulo = body.titulo === undefined ? undefined : String(body.titulo || "").trim();
  const mensagem = body.mensagem === undefined ? undefined : String(body.mensagem || "").trim();
  const prioridade = body.prioridade === undefined ? undefined : String(body.prioridade || "normal").trim();
  const fixado = body.fixado === undefined ? undefined : Boolean(body.fixado);
  const ativo = body.ativo === undefined ? undefined : Boolean(body.ativo);

  if (!partial || titulo !== undefined) {
    if (!titulo || titulo.length < 3) {
      return { ok: false, message: "Titulo precisa ter pelo menos 3 caracteres." };
    }
  }

  if (!partial || mensagem !== undefined) {
    if (!mensagem || mensagem.length < 3) {
      return { ok: false, message: "Mensagem precisa ter pelo menos 3 caracteres." };
    }
  }

  if (prioridade !== undefined && !["normal", "importante", "urgente"].includes(prioridade)) {
    return { ok: false, message: "Prioridade invalida." };
  }

  const data = {};

  if (titulo !== undefined) data.titulo = titulo;
  if (mensagem !== undefined) data.mensagem = mensagem;
  if (prioridade !== undefined) data.prioridade = prioridade;
  if (fixado !== undefined) data.fixado = fixado;
  if (ativo !== undefined) data.ativo = ativo;

  return { ok: true, data };
}

router.get("/", requireAuth, async (req, res) => {
  const includeInactive = req.auth.profile.role === "professor" && req.query.includeInactive === "true";
  let query = supabase
    .from("recados")
    .select("id, titulo, mensagem, prioridade, fixado, ativo, created_by, created_at, updated_at")
    .order("fixado", { ascending: false })
    .order("created_at", { ascending: false });

  if (!includeInactive) {
    query = query.eq("ativo", true);
  }

  const { data, error } = await query;

  if (error) {
    return res.status(500).json({ error: "Nao foi possivel carregar os recados." });
  }

  return res.json({ recados: data });
});

router.post("/", requireAuth, requireProfessor, async (req, res) => {
  const validation = validateRecado(req.body);

  if (!validation.ok) {
    return res.status(400).json({ error: validation.message });
  }

  const { data, error } = await supabase
    .from("recados")
    .insert({
      ...validation.data,
      created_by: req.auth.profile.id,
    })
    .select("id, titulo, mensagem, prioridade, fixado, ativo, created_by, created_at, updated_at")
    .single();

  if (error) {
    return res.status(500).json({ error: "Nao foi possivel criar o recado." });
  }

  return res.status(201).json({
    message: "Recado criado com sucesso.",
    recado: data,
  });
});

router.patch("/:id", requireAuth, requireProfessor, async (req, res) => {
  const validation = validateRecado(req.body, true);

  if (!validation.ok) {
    return res.status(400).json({ error: validation.message });
  }

  if (Object.keys(validation.data).length === 0) {
    return res.status(400).json({ error: "Informe ao menos um campo para atualizar." });
  }

  const { data, error } = await supabase
    .from("recados")
    .update(validation.data)
    .eq("id", req.params.id)
    .select("id, titulo, mensagem, prioridade, fixado, ativo, created_by, created_at, updated_at")
    .single();

  if (error) {
    return res.status(500).json({ error: "Nao foi possivel atualizar o recado." });
  }

  return res.json({
    message: "Recado atualizado com sucesso.",
    recado: data,
  });
});

router.delete("/:id", requireAuth, requireProfessor, async (req, res) => {
  const { data, error } = await supabase
    .from("recados")
    .update({ ativo: false })
    .eq("id", req.params.id)
    .select("id, titulo, mensagem, prioridade, fixado, ativo, created_by, created_at, updated_at")
    .single();

  if (error) {
    return res.status(500).json({ error: "Nao foi possivel desativar o recado." });
  }

  return res.json({
    message: "Recado desativado com sucesso.",
    recado: data,
  });
});

module.exports = router;

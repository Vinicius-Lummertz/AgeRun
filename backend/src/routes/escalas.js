const express = require("express");
const { requireAuth, requireProfessor } = require("../middleware/auth");
const { supabase } = require("../lib/supabase");

const router = express.Router();

function validateEscala(body) {
  const titulo = String(body.titulo || "").trim();
  const descricao = String(body.descricao || "").trim();
  const local = String(body.local || "").trim();
  const inicioAt = String(body.inicio_at || body.inicioAt || "").trim();
  const fimAt = String(body.fim_at || body.fimAt || "").trim();

  if (titulo.length < 3) {
    return { ok: false, message: "Titulo precisa ter pelo menos 3 caracteres." };
  }

  if (!inicioAt || Number.isNaN(Date.parse(inicioAt))) {
    return { ok: false, message: "Informe uma data/hora de inicio valida." };
  }

  if (fimAt && Number.isNaN(Date.parse(fimAt))) {
    return { ok: false, message: "Data/hora de fim invalida." };
  }

  if (fimAt && Date.parse(fimAt) <= Date.parse(inicioAt)) {
    return { ok: false, message: "O fim precisa ser depois do inicio." };
  }

  return {
    ok: true,
    data: {
      titulo,
      descricao: descricao || null,
      local: local || null,
      inicio_at: new Date(inicioAt).toISOString(),
      fim_at: fimAt ? new Date(fimAt).toISOString() : null,
    },
  };
}

router.get("/", requireAuth, async (_req, res) => {
  const { data, error } = await supabase
    .from("escalas")
    .select("id, titulo, descricao, local, inicio_at, fim_at, created_by, created_at")
    .order("inicio_at", { ascending: true });

  if (error) {
    return res.status(500).json({ error: "Nao foi possivel carregar as escalas." });
  }

  return res.json({ escalas: data });
});

router.post("/", requireAuth, requireProfessor, async (req, res) => {
  const validation = validateEscala(req.body);

  if (!validation.ok) {
    return res.status(400).json({ error: validation.message });
  }

  const { data, error } = await supabase
    .from("escalas")
    .insert({
      ...validation.data,
      created_by: req.auth.profile.id,
    })
    .select("id, titulo, descricao, local, inicio_at, fim_at, created_by, created_at")
    .single();

  if (error) {
    return res.status(500).json({ error: "Nao foi possivel criar a escala." });
  }

  return res.status(201).json({
    message: "Escala criada com sucesso.",
    escala: data,
  });
});

module.exports = router;

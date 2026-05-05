const path = require("path");

require("dotenv").config({ path: path.resolve(__dirname, "../.env"), quiet: true });
require("dotenv").config({ path: path.resolve(__dirname, ".env"), quiet: true });

const cors = require("cors");
const express = require("express");
const cadastroRouter = require("./routes/cadastro");
const escalasRouter = require("./routes/escalas");
const loginRouter = require("./routes/login");

const app = express();
const port = Number(process.env.PORT || 3000);

app.use(cors());
app.use(express.json());

app.get("/health", (_req, res) => {
  res.json({ ok: true, service: "agerun-backend" });
});

app.use("/cadastro", cadastroRouter);
app.use("/login", loginRouter);
app.use("/escalas", escalasRouter);

app.use((req, res) => {
  res.status(404).json({ error: `Rota ${req.method} ${req.path} não encontrada.` });
});

app.use((err, _req, res, _next) => {
  console.error(err);
  res.status(500).json({ error: "Erro interno do servidor." });
});

const server = app.listen(port, () => {
  console.log(`AgeRun backend rodando em http://localhost:${port}`);
});

server.on("error", (err) => {
  if (err.code === "EADDRINUSE") {
    console.error(`Porta ${port} ja esta em uso. Encerre o processo antigo ou altere PORT no .env.`);
    process.exit(1);
  }

  throw err;
});

import { readFile } from "node:fs/promises";
import { resolve } from "node:path";
import pg from "pg";

const { Client } = pg;

const databaseUrl = process.env.SUPABASE_DB_URL;
const sqlFile = process.argv[2] ?? "supabase/migrations/202606170001_agego_full_schema.sql";

if (!databaseUrl) {
  console.error("Set SUPABASE_DB_URL with the Supabase Postgres connection string.");
  process.exit(1);
}

const filePath = resolve(sqlFile);
const sql = await readFile(filePath, "utf8");
const client = new Client({
  connectionString: databaseUrl,
  ssl: { rejectUnauthorized: false }
});

try {
  await client.connect();
  const statements = splitSqlStatements(sql);
  for (let index = 0; index < statements.length; index += 1) {
    const statement = statements[index];
    if (!statement.trim()) continue;
    try {
      await client.query(statement);
    } catch (error) {
      error.message = `Statement ${index + 1}/${statements.length} failed: ${error.message}`;
      throw error;
    }
  }
  console.log(`Applied ${sqlFile}`);
} finally {
  await client.end();
}

function splitSqlStatements(input) {
  const statements = [];
  let current = "";
  let singleQuote = false;
  let doubleQuote = false;
  let lineComment = false;
  let blockComment = false;
  let dollarTag = null;

  for (let index = 0; index < input.length; index += 1) {
    const char = input[index];
    const next = input[index + 1];

    if (lineComment) {
      current += char;
      if (char === "\n") lineComment = false;
      continue;
    }

    if (blockComment) {
      current += char;
      if (char === "*" && next === "/") {
        current += next;
        index += 1;
        blockComment = false;
      }
      continue;
    }

    if (dollarTag) {
      if (input.startsWith(dollarTag, index)) {
        current += dollarTag;
        index += dollarTag.length - 1;
        dollarTag = null;
      } else {
        current += char;
      }
      continue;
    }

    if (!singleQuote && !doubleQuote) {
      if (char === "-" && next === "-") {
        current += char + next;
        index += 1;
        lineComment = true;
        continue;
      }
      if (char === "/" && next === "*") {
        current += char + next;
        index += 1;
        blockComment = true;
        continue;
      }
      if (char === "$") {
        const rest = input.slice(index);
        const match = rest.match(/^\$[A-Za-z_][A-Za-z0-9_]*\$|^\$\$/);
        if (match) {
          dollarTag = match[0];
          current += dollarTag;
          index += dollarTag.length - 1;
          continue;
        }
      }
    }

    if (char === "'" && !doubleQuote) {
      singleQuote = !singleQuote;
      current += char;
      continue;
    }

    if (char === "\"" && !singleQuote) {
      doubleQuote = !doubleQuote;
      current += char;
      continue;
    }

    if (char === ";" && !singleQuote && !doubleQuote) {
      current += char;
      statements.push(current);
      current = "";
      continue;
    }

    current += char;
  }

  if (current.trim()) statements.push(current);
  return statements;
}

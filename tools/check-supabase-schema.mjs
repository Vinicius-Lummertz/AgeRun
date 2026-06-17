import pg from "pg";

const { Client } = pg;
const databaseUrl = process.env.SUPABASE_DB_URL;

if (!databaseUrl) {
  console.error("Set SUPABASE_DB_URL with the Supabase Postgres connection string.");
  process.exit(1);
}

const client = new Client({
  connectionString: databaseUrl,
  ssl: { rejectUnauthorized: false }
});

const expectedTables = [
  "users",
  "student_profiles",
  "groups",
  "group_members",
  "community_posts",
  "community_post_comments",
  "management_files"
];

try {
  await client.connect();
  const tables = await client.query(
    `select table_name
       from information_schema.tables
      where table_schema = 'public'
        and table_name = any($1)
      order by table_name`,
    [expectedTables]
  );
  const buckets = await client.query(
    `select id from storage.buckets where id in ('agego-social-posts', 'agego-management') order by id`
  );
  const functions = await client.query(
    `select proname
       from pg_proc p
       join pg_namespace n on n.oid = p.pronamespace
      where n.nspname = 'public'
        and proname in ('get_instructor_students', 'set_group_members')
      order by proname`
  );

  console.log(`tables=${tables.rows.map((row) => row.table_name).join(",")}`);
  console.log(`buckets=${buckets.rows.map((row) => row.id).join(",")}`);
  console.log(`functions=${functions.rows.map((row) => row.proname).join(",")}`);
} finally {
  await client.end();
}

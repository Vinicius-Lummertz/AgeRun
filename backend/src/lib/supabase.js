const path = require("path");

require("dotenv").config({ path: path.resolve(__dirname, "../../.env"), quiet: true });
require("dotenv").config({ path: path.resolve(__dirname, "../.env"), quiet: true });
require("dotenv").config({ path: path.resolve(__dirname, ".env"), quiet: true });

const { createClient } = require("@supabase/supabase-js");

const supabaseUrl = process.env.SUPABASE_URL;
const supabaseServiceRoleKey = process.env.SUPABASE_SERVICE_ROLE_KEY;

if (!supabaseUrl || !supabaseServiceRoleKey) {
  throw new Error("SUPABASE_URL e SUPABASE_SERVICE_ROLE_KEY precisam estar configurados.");
}

const supabase = createClient(supabaseUrl, supabaseServiceRoleKey, {
  auth: {
    autoRefreshToken: false,
    persistSession: false,
  },
});

module.exports = { supabase };

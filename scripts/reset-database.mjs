import 'dotenv/config';
import { createClient } from '@supabase/supabase-js';

const supabaseUrl = process.env.SUPABASE_URL;
const serviceRoleKey = process.env.SUPABASE_SERVICE_ROLE_KEY;

if (!supabaseUrl || !serviceRoleKey) {
  throw new Error('SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY are required.');
}

const supabase = createClient(supabaseUrl, serviceRoleKey, {
  auth: { autoRefreshToken: false, persistSession: false }
});

const uuidZero = '00000000-0000-0000-0000-000000000000';
const deletePlan = [
  ['community_poll_votes', 'option_id', uuidZero],
  ['community_comment_likes', 'comment_id', uuidZero],
  ['community_post_comments', 'id', uuidZero],
  ['community_poll_options', 'id', uuidZero],
  ['community_post_events', 'post_id', uuidZero],
  ['community_post_students', 'post_id', uuidZero],
  ['community_post_groups', 'post_id', uuidZero],
  ['community_post_likes', 'post_id', uuidZero],
  ['community_post_shares', 'post_id', uuidZero],
  ['community_posts', 'id', uuidZero],
  ['student_charges', 'id', uuidZero],
  ['workout_sessions', 'id', uuidZero],
  ['student_presence', 'student_id', uuidZero],
  ['workout_activities', 'id', uuidZero],
  ['announcements', 'id', uuidZero],
  ['events', 'id', uuidZero],
  ['groups', 'id', uuidZero],
  ['routines', 'id', uuidZero],
  ['workouts', 'id', uuidZero],
  ['student_profiles', 'id', uuidZero],
  ['instructor_settings', 'instructor_id', uuidZero],
  ['auth_verification_tokens', 'id', uuidZero],
  ['auth_pending_registrations', 'id', uuidZero],
  ['users', 'id', uuidZero]
];

async function deleteRows(table, column, value) {
  const { error, count } = await supabase
    .from(table)
    .delete({ count: 'exact' })
    .neq(column, value);

  if (error) {
    if (error.code === '42P01' || /does not exist/i.test(error.message)) {
      console.log(`skip ${table}: missing`);
      return;
    }
    throw new Error(`${table}: ${error.message}`);
  }
  console.log(`cleared ${table}: ${count ?? 0}`);
}

async function deleteAuthUsers() {
  let page = 1;
  let deleted = 0;

  while (true) {
    const { data, error } = await supabase.auth.admin.listUsers({ page, perPage: 100 });
    if (error) throw error;

    const users = data?.users ?? [];
    if (users.length === 0) break;

    for (const user of users) {
      const { error: deleteError } = await supabase.auth.admin.deleteUser(user.id);
      if (deleteError) throw deleteError;
      deleted += 1;
      console.log(`deleted auth user: ${user.id}`);
    }

    if (users.length < 100) break;
  }

  console.log(`cleared auth.users: ${deleted}`);
}

for (const [table, column, value] of deletePlan) {
  await deleteRows(table, column, value);
}
await deleteAuthUsers();

console.log('database reset complete');

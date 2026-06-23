CREATE TABLE IF NOT EXISTS public.instructor_settings (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  instructor_id UUID UNIQUE NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  pix_key VARCHAR(140) DEFAULT '',
  pix_owner_name VARCHAR(255) DEFAULT '',
  pix_document VARCHAR(20) DEFAULT '',
  address_street VARCHAR(255) DEFAULT '',
  address_number VARCHAR(20) DEFAULT '',
  address_neighborhood VARCHAR(255) DEFAULT '',
  address_city VARCHAR(100) DEFAULT '',
  address_state VARCHAR(2) DEFAULT '',
  address_zip_code VARCHAR(9) DEFAULT '',
  notification_email BOOLEAN DEFAULT TRUE,
  notification_push BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

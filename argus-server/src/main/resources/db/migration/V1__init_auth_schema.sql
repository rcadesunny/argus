create table users (
                       id            uuid primary key,
                       email         text not null,
                       password_hash text not null,
                       status        text not null,
                       created_at    timestamptz not null default now(),
                       updated_at    timestamptz not null default now()
);

create unique index ux_users_email_lower on users (lower(email));

create table roles (
                       id   uuid primary key,
                       name text not null unique
);

create table permissions (
                             id   uuid primary key,
                             name text not null unique
);

create table user_roles (
                            user_id uuid not null references users (id) on delete cascade,
                            role_id uuid not null references roles (id) on delete cascade,
                            primary key (user_id, role_id)
);

create table role_permissions (
                                  role_id       uuid not null references roles (id) on delete cascade,
                                  permission_id uuid not null references permissions (id) on delete cascade,
                                  primary key (role_id, permission_id)
);

create table refresh_tokens (
                                id         uuid primary key,
                                user_id    uuid not null references users (id) on delete cascade,
                                token_hash text not null unique,
                                family_id  uuid not null,
                                expires_at timestamptz not null,
                                used_at    timestamptz,
                                revoked_at timestamptz,
                                created_at timestamptz not null default now()
);

create index ix_refresh_tokens_family on refresh_tokens (family_id);
create index ix_refresh_tokens_user on refresh_tokens (user_id);

create table auth_identities (
                                 id               uuid primary key,
                                 user_id          uuid not null references users (id) on delete cascade,
                                 provider         text not null,
                                 provider_user_id text not null,
                                 created_at       timestamptz not null default now(),
                                 unique (provider, provider_user_id)
);


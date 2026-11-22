insert into roles (id, name)
values (gen_random_uuid(), 'USER'),
       (gen_random_uuid(), 'ADMIN')
    on conflict (name) do nothing;

insert into permissions (id, name)
values (gen_random_uuid(), 'user:read'),
       (gen_random_uuid(), 'user:write')
    on conflict (name) do nothing;

insert into role_permissions (role_id, permission_id)
select r.id, p.id
from roles r
         join permissions p on p.name = 'user:read'
where r.name = 'USER'
    on conflict do nothing;

insert into role_permissions (role_id, permission_id)
select r.id, p.id
from roles r
         join permissions p on p.name in ('user:read', 'user:write')
where r.name = 'ADMIN'
    on conflict do nothing;
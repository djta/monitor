insert into ${schema}s_role (role_id, app_id, rolename, description)
values(${seq:nextval@${schema}seq_role}, ${seq:currval@${schema}seq_application}, '基本用户', '基本用户')

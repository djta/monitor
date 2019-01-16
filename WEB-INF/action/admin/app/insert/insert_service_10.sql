insert into ${schema}s_service (service_id, path, description, description_cn, description_en, app_id, group_id) 
values (${seq:nextval@${schema}seq_service}, '/action/security/newpass/form', '更改我的密码', '更改我的密码', 'Change My Passwd', ${seq:currval@${schema}seq_application}, ${seq:currval@${schema}seq_service_group})

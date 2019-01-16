insert into ${schema}s_service (service_id, path, description, description_cn, description_en, app_id, group_id) 
values (${seq:nextval@${schema}seq_service}, '/action/security/exit', '退出系统', '退出系统', 'Exit', ${seq:currval@${schema}seq_application}, ${seq:currval@${schema}seq_service_group})

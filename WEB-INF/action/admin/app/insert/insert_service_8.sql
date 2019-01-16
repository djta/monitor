insert into ${schema}s_service (service_id, path, description, description_cn, description_en, app_id, group_id) 
values (${seq:nextval@${schema}seq_service}, '/action/security/loginok', '登录成功界面', '登录成功界面', 'Login OK', ${seq:currval@${schema}seq_application}, ${seq:currval@${schema}seq_service_group})

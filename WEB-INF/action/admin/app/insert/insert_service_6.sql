insert into ${schema}s_service (service_id, path, description, description_cn, description_en, app_id, group_id) 
values (${seq:nextval@${schema}seq_service}, '/action/security/loginhistory/search', '登录历史查询', '登录历史查询', 'Search Login History', ${seq:currval@${schema}seq_application}, ${seq:currval@${schema}seq_service_group})

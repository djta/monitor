insert into ${schema}s_service (service_id, path, description, description_cn, description_en, app_id, group_id) 
values (${seq:nextval@${schema}seq_service}, '/action/security/profile/edit', '我的基本信息', '我的基本信息', 'My Profile', ${seq:currval@${schema}seq_application}, ${seq:currval@${schema}seq_service_group})

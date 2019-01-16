insert into ${schema}s_service (service_id, path, description, description_cn, description_en, app_id, group_id) 
values (${seq:nextval@${schema}seq_service}, '/action/security/profile/update', '更新我的基本信息', '更新我的基本信息', 'Update Profile', ${seq:currval@${schema}seq_application}, ${seq:currval@${schema}seq_service_group})

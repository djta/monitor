insert into ${schema}s_service_group 
(group_id, app_id, group_name)
values
(${seq:nextval@${schema}seq_service_group},${seq:currval@${schema}seq_application},'基本权限')

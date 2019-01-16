insert into ${schema}s_service_role (service_role_id, service_id, role_id)
select ${seq:nextval@${schema}seq_service_role}, service_id, ${seq:currval@${schema}seq_role} from ${schema}s_service where app_id = ${seq:currval@${schema}seq_application}

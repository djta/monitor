UPDATE t_imp_data_log 
SET 
	updated = {ts '${def:timestamp}'},
	resultcode = ${import_result},
	resultdesc = '${result_desc}' 
WHERE tuid=${import_log_tuid}
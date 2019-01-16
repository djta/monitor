INSERT INTO t_imp_data_log(
	tuid,
	import_batch,
	file_name,
	file_path,
	resultcode,
	created,
	createdby,
	org_id
) VALUES(
	'${import_log_tuid}',
	'${import_batch}',
	'${file_name}',
	'${file_path}',
	0,
	{ts '${def:timestamp}'},
	'${def:user}',
	${def:org}
)
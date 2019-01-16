insert into t_attachment_files(
	tuid,
	file_name,
	file_type,
	file_path,
	file_size,
	created,
	createdby,
	pk_value,
	table_code,
	org_id
) values(
	${seq:nextval@seq_t_attachment_files},
	${fld:file.filename},
	${fld:file.content-type},
	${fld:file.path},
	${fld:file_size},
	{ts '${def:timestamp}'},
	'${def:user}',
	${fld:pk_value},
	${fld:table_code},
	${def:org}
)
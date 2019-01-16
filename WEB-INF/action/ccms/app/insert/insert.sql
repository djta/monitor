INSERT INTO cc_app_version(
	tuid,
	app_code,
	version_no,
	url,
	is_deleted,
	created,
	download_count
) VALUES(
	${seq:nextval@seq_cc_app_version},
	(CASE WHEN ${fld:app_code} IS NULL OR ${fld:app_code} = '' THEN 'app' ELSE ${fld:app_code} END),
	${fld:version_no},
	'${url}',
	0,
	{ts '${def:timestamp}'},
	0
)
SELECT 
	tuid,
	app_code,
	version_no,
	created 
FROM cc_app_version 
WHERE is_deleted = 0
${orderby}
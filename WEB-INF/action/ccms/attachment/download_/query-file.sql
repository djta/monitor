SELECT
	ST_FILE_NAME file_name,
	ST_FILE_PATH file_path,
	ST_FILE_TYPE file_type
FROM
	RF_FILE_URL
where 
	in_file_id=${fld:id}

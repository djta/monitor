update t_attachment_files 
set 
	table_code = 'hr_org'
	,pk_value = ${seq:currval@seq_hr_org}
where tuid = ${fld:upload_id}

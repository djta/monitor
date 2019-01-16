UPDATE t_attachment_files 
SET 
	pk_value = ${seq:currval@${schema}seq_user} 
WHERE tuid = ${fld:upload_id}
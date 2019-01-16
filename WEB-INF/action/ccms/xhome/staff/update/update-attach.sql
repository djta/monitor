UPDATE t_attachment_files 
SET 
	pk_value = ${fld:user_id} 
WHERE tuid = ${fld:upload_id}
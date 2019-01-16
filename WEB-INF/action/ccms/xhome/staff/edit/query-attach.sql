SELECT tuid AS upload_id FROM t_attachment_files 
WHERE pk_value = ${fld:id}::varchar AND table_code = 'hr_staff' 
ORDER BY tuid

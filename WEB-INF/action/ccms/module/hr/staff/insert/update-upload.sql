update 
t_attachment_files
set
table_code='hr_staff'
,pk_value=${seq:currval@${schema}seq_user}
where
tuid=${fld:upload_id}

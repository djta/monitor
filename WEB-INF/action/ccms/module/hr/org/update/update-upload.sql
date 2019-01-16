update 
t_attachment_files
set
table_code='hr_org'
,pk_value=${fld:tuid}
where
tuid=${fld:upload_id}

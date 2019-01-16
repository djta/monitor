update 
t_attachment_files
set
table_code='hr_staff'
,pk_value=(select user_id from hr_staff where userlogin=${fld:userlogin})
where
tuid=${fld:upload_id}

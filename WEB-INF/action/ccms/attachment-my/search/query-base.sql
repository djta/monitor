select
   	ta.tuid,
    ta.file_name,
    ta.file_path,
    (file_size / 1024) as file_size,
    ta.created,
    (case when ta.file_type ='application/x-jpe' or ta.file_type ='application/x-jpg' or ta.file_type ='image/jpeg' or ta.file_type ='image/png' then file_path else file_type_name end) file_type_name
from 
    t_attachment_files ta
left join file_dictionary fd ON fd.type_code = ta.file_type
where
 1=1
 ${filter}
${orderby}

select
   	tuid,
    file_name,
    file_path,
    (file_size / 1024) as file_size,
    created
from 
    t_attachment_files
where
   createdby = '${def:user}'
and 
	is_system = '0'

${orderby}
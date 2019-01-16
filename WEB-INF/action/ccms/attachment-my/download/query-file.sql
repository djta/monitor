select
	file_name
	,file_path
	,file_type
from 
	t_attachment_files
where 
	tuid=${fld:id}

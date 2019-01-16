select
   	tuid,
    file_path
from 
    t_attachment_files
where
	is_system = '1'
and
	file_show_type = ${fld:file_show_type}	--标签页
	
	${filter}
	${orderby}
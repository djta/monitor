select 
	${seq:currval@seq_t_attachment_files} as id, 
	${fld:file.filerealname}::varchar as name, 
	${fld:file.content-type}::varchar as filetype 
from dual
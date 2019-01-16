select
	f.*
from 
	t_document f
where 
	f.tuid = ${fld:document_id}
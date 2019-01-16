select
	t.*
from 
	t_document_params t
	inner join t_document d on d.tuid = t.document_id
where 
	d.tuid = ${fld:document_id}

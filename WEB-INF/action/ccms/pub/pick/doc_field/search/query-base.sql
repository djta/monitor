select 
	p.tuid    as  id
	, p.field_name_cn as description
	, p.field_code as name
from 
	t_field p
where
	p.table_id = (select table_id from t_document where tuid=${fld:document_id})
	
	${filter}
	${orderby}
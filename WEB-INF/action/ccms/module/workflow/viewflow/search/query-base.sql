select
	tuid,
	wfm_name,
	logo_path,
	(select document_id from os_wfm_node where wfm_id = os_wfm.tuid and node_type='0') as document_id
from 
	os_wfm
where 
	tenantry_id = ${def:tenantry}
and
	wfm_status = '1'
and 
	xml_release is not null
	
	${filter}
	${orderby}
select 
	tuid,
	wfm_name,
	(case when wfm_status='0' then '禁用' else '启用' end) as wfm_status,
	remark,
	wfm_type,
	(case when xml_release is null or xml_release='' then '0' else '1' end) as xml_release,
	(select document_id from os_wfm_node where wfm_id = os_wfm.tuid and node_type='0') as document_id
from 
	os_wfm
where 
	tenantry_id = ${def:tenantry}
	
	${filter}
	${orderby}
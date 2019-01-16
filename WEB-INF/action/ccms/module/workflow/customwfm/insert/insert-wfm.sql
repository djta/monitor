insert into os_wfm
(
	tuid
	,wfm_name
	,wfm_status
	,remark
	,created
	,createdby
	,table_id
	,xml_value
	,tenantry_id
	,is_template
	,wfm_real_name
	,logo_path
)
values
(
	${seq:nextval@${schema}seq_default}
	,${fld:wfm_name}
	,'1'
	,${fld:remark}
	,{ts '${def:timestamp}'}
	,'${def:user}'
	,${fld:table_id}
	,'{"flow": {"wfm_id": "' || ${seq:currval@${schema}seq_default} || '","wfm_name": "' || ${fld:wfm_name} || '","table_id": "' || ${fld:table_id} || '","table_name": "' || ${fld:table_name} || '","wfm_status": "0", "is_template": "0","remark": "null","max": 39}}'
	,${def:tenantry}
	,'0'
	,concat('${def:tenantry}_',${fld:wfm_name})
	,${fld:logo_path}
)
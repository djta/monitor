select
	${seq:nextval@${schema}seq_default} as tuid
	,${fld:wfm_name}::varchar as wfm_name
	,${fld:remark}::varchar as remark
	,table_id
	,xml_value
	,${def:tenantry} as tenantry_id
	,concat(concat(${def:tenantry},'_'),${fld:wfm_name})::varchar as wfm_real_name
from
	os_wfm
where 
	tuid = ${fld:template_id}
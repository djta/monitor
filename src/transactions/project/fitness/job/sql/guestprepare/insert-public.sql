insert into cc_public(
	tuid
	,datatype
	,guestcode
	,customercode
	,entertime
	,oldfollow
	,reason
	,status
	,org_id
) values(
	nextval('seq_cc_public')
	,${datatype}
	,${fld:guestcode}
	,${fld:customercode}
	,{ts '${def:timestamp}'}
	,${fld:mc}
	,'${resason}'
	,0
	,${fld:org_id}
)
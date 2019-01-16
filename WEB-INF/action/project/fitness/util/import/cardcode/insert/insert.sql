insert into cc_cardcode(
	tuid
	,incode
	,cardcode
	,status
	,remark
	,org_id
) values(
	${seq:nextval@seq_cc_cardcode}
	,${fld:vc_guid}
	,${fld:vc_cardcode}
	,1
	,${fld:vc_remark}
	,${def:tenantry}
)
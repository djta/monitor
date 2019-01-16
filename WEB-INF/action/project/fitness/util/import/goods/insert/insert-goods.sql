insert into e_productdef(
	vc_code
	,vc_pcode
	,vc_name
	,vc_fastcode
	,vc_standard
	,vc_unit
	,f_buyprice
	,vc_type
	,vc_isgift
	,vc_txcode
	,i_status 
	,vc_club
) values(
	${seq:nextval@seq_e_productdef}
	,${fld:vc_pcode}
	,${fld:vc_name}
	,${fld:vc_fastcode}
	,${fld:vc_standard}
	,${fld:vc_unit}
	,${fld:f_buyprice}
	,${fld:vc_type}
	,'0'
	,${fld:vc_txcode}
	,1 
	,'${def:org}'
)
INSERT INTO hr_menu_btn(
	tuid
	,menu_id
	,uri
	,btn_name
	,btn_id
	,is_deleted
	,createdby
	,created
) VALUES(
	${seq:nextval@seq_hr_menu_btn}
	,${fld:menu_id}
	,${fld:uri}
	,${fld:btn_name}
	,${fld:btn_id}
	,0
	,'${def:user}'
	,{ts '${def:timestamp}'}
)

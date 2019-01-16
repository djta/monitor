INSERT INTO hr_staff_quickmenu(
	tuid
	,menu_id
	,user_id
	,userlogin
	,created
	,quick_type
) VALUES(
	${seq:nextval@seq_hr_staff_quickmenu}
	,${fld:menu_id}
	,(SELECT user_id FROM hr_staff WHERE userlogin = '${def:user}' LIMIT 1)
	,'${def:user}'
	,{ts '${def:timestamp}'}
	,0
)
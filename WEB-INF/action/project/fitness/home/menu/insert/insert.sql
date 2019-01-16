INSERT INTO hr_menu(
	tuid
	,pid
	,menu_type
	,menu_name
	,menu_grade
	,icon_path
	,icon_path2
	,uri
	,show_order
	,is_deleted
	,createdby
	,created
) VALUES(
	${seq:nextval@seq_hr_menu}
	,${fld:pid}
	,${fld:menu_type}
	,${fld:menu_name}
	,(CASE WHEN ${fld:pid} = 0 THEN 1 ELSE (SELECT menu_grade FROM hr_menu WHERE tuid=${fld:pid})+1 END)
	,${fld:icon_path}
	,${fld:icon_path2}
	,${fld:uri}
	,(CASE WHEN ${fld:show_order} IS NULL THEN 0 ELSE ${fld:show_order} END)
	,0
	,'${def:user}'
	,{ts '${def:timestamp}'}
)

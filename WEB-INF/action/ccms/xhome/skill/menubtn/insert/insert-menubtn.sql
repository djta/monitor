INSERT INTO hr_skill_menu_btn(
	tuid
	,skill_id
	,menu_btn_id
) VALUES(
	${seq:nextval@seq_hr_skill_menu}
	,${fld:skill_id}
	,${fld:menu_btn_id}
)

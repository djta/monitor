INSERT INTO hr_skill_menu(
	tuid
	,skill_id
	,menu_id
) VALUES(
	${seq:nextval@seq_hr_skill_menu}
	,${seq:currval@seq_hr_skill}
	,${fld:menu_id}
)

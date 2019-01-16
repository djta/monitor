/** 无按钮访问权限 */
select 
	a.uri
	,a.btn_name
	,a.btn_id 
from hr_menu_btn a 
inner join hr_skill_menu c on a.menu_id = c.menu_id 
inner join hr_menu m on m.tuid = a.menu_id 
left join hr_skill_menu_btn b on a.tuid = b.menu_btn_id and b.skill_id = c.skill_id 
where (
	case when a.uri is not null and a.uri != '' then (
		a.uri = ${fld:uri} or a.uri like concat(${fld:uri}, ';%') or a.uri like concat('%;', ${fld:uri}) or a.uri like concat('%;', ${fld:uri}, ';%') 
	) else (
		m.uri = ${fld:uri} or m.uri like concat(${fld:uri}, '?%')
	) end
) 
and b.menu_btn_id is null and exists (
	select 1 from hr_staff s 
	inner join hr_staff_skill sk on s.user_id = sk.user_id 
	inner join hr_skill k on sk.skill_id = k.skill_id 
	where s.userlogin = '${def:user}' and sk.skill_id = c.skill_id  
) and not exists (
	/** 有按钮访问权限 */
	select 1 from hr_menu_btn a1 
	inner join hr_skill_menu c1 on a1.menu_id = c1.menu_id 
	inner join hr_menu m1 on m1.tuid = a1.menu_id 
	inner join hr_skill_menu_btn b1 on a1.tuid = b1.menu_btn_id and b1.skill_id = c1.skill_id 
	where (
		case when a1.uri is not null and a1.uri != '' then (
			a1.uri = ${fld:uri} or a1.uri like concat(${fld:uri}, ';%') or a1.uri like concat('%;', ${fld:uri}) or a1.uri like concat('%;', ${fld:uri}, ';%') 
		) else (
			m1.uri = ${fld:uri} or m1.uri like concat(${fld:uri}, '?%')
		) end
	) 
	and exists (
		select 1 from hr_staff s 
		inner join hr_staff_skill sk on s.user_id = sk.user_id 
		inner join hr_skill k on sk.skill_id = k.skill_id 
		where s.userlogin = '${def:user}' and sk.skill_id = c1.skill_id  
	) and a.btn_id = a1.btn_id
)


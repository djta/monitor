select 
	count(1) as tice
from cc_testresult tr
inner join (select 
			staff.userlogin
		from hr_staff staff
		left join hr_staff_skill ss on staff.user_id = ss.user_id
		inner join (select skill_id from hr_skill sk left join cc_target_group tg on sk.skill_id = tg.pk_value and sk.org_id = tg.org_id
				where sk.org_id = ${def:org} and sk.skill_scope = ${fld:skill_scope}
				and tg.target_type=0 and concat(tg.target_year||'-'||tg.target_month)= (select to_char(('${def:date}'::date- ('1 month')::interval), 'yyyy-MM'))
			) skill on skill.skill_id = ss.skill_id
		where staff.org_id = ${def:org} 
		and staff.status=1) s on tr.createdby = s.userlogin
where tr.org_id = ${def:org} and 
	 to_char(tr.created::date, 'yyyy-MM') = (select to_char(('${def:date}'::date- ('1 month')::interval), 'yyyy-MM'))

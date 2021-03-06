select 
	count(1) as chengdanliang
from cc_contract c 
inner join cc_customer m on m.code=c.guestcode and c.org_id = m.org_id
inner join cc_guest g on g.code=m.guestcode and g.org_id = m.org_id
inner join (select 
			staff.userlogin
		from hr_staff staff
		left join hr_staff_skill ss on staff.user_id = ss.user_id
		inner join (select skill_id from hr_skill sk left join cc_target_group tg on sk.skill_id = tg.pk_value and sk.org_id = tg.org_id
				where sk.org_id = ${def:org} and sk.skill_scope = ${fld:skill_scope}
				and tg.target_type=0 and concat(tg.target_year||'-'||tg.target_month)= (select to_char(('${def:date}'::date- ('1 month')::interval), 'yyyy-MM'))
			) skill on skill.skill_id = ss.skill_id
		where staff.org_id = ${def:org} 
		and staff.status=1) s on c.createdby = s.userlogin
where c.org_id = ${def:org} 
and c.contracttype !=3 and c.status = 2 
and m.status != 0 

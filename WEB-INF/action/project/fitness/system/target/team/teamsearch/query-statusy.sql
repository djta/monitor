select 
	count(1) as yuyuenum
from cc_guest g 
inner join cc_guest_prepare p on g.code = p.guestcode 
and p.status != 0 and g.org_id = p.org_id
inner join (select 
			staff.userlogin
		from hr_staff staff
		left join hr_team_staff ss on staff.userlogin = ss.userlogin
		inner join (select team_id from hr_team sk left join cc_target_group tg on sk.team_id = tg.pk_value and sk.org_id = tg.org_id
			where sk.org_id = ${def:org} and sk.skill_scope = ${fld:skill_scope}
			and tg.target_type=0 and concat(tg.target_year||'-'||tg.target_month)= (select to_char(('${def:date}'::date- ('1 month')::interval), 'yyyy-MM'))
		) team on team.team_id = ss.team_id
		where staff.org_id = ${def:org} 
		and staff.status=1) s on p.createdby = s.userlogin
where g.org_id = ${def:org} and 
	 to_char(p.preparedate::date, 'yyyy-MM') = (select to_char(('${def:date}'::date- ('1 month')::interval), 'yyyy-MM'))

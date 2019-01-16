select sum(a.huifang) as huifanghuiji from
(
select 
	count(1) as huifang
from cc_guest guest
where guest.status=1 and guest.org_id = ${def:org}
and exists(
		select 1 from cc_return_log log 
		inner join cc_comm c on log.commcode=c.code and log.org_id = c.org_id
		inner join (select 
			staff.userlogin
		from hr_staff staff
		left join hr_team_staff ss on staff.userlogin = ss.userlogin
		inner join (select team_id from hr_team sk left join cc_target_group tg on sk.team_id = tg.pk_value and sk.org_id = tg.org_id
			where sk.org_id = ${def:org} and sk.skill_scope = ${fld:skill_scope}
			and tg.target_type=0 and concat(tg.target_year||'-'||tg.target_month)= (select to_char(('${def:date}'::date- ('1 month')::interval), 'yyyy-MM'))
		) team on team.team_id = ss.team_id
		where staff.org_id = ${def:org} 
		and staff.status=1) s on log.followby = s.userlogin
		where log.org_id = ${def:org}
		and log.custtype=0 and log.coMMcode is not null and log.coMMcode!=''
		and log.pk_value = guest.code
		and to_char(log.created, 'yyyy-MM')=to_char('${def:date}'::date, 'yyyy-MM')
		and c.operatortype=0
	)

union
--一月之内要过期不续费的会员
select 
	count(1) as huifang
from cc_customer cust
where cust.org_id = ${def:org} 
and 30 >= (
	select max(d.enddate) from cc_card d where d.isgoon=0 and d.org_id = cust.org_id 
	and d.customercode = cust.code 
	and d.status !=0 and d.status!=6 
	and d.enddate is not null 
) - {d '${def:date}'} 
and not exists(
	select 1 from cc_card d2 where
	d2.customercode = cust.code  and d2.isgoon=1
) 
and exists(
		select 1 from cc_return_log  log 
		inner join cc_comm c on log.commcode=c.code and log.org_id = c.org_id
		inner join (select 
			staff.userlogin
		from hr_staff staff
		left join hr_team_staff ss on staff.userlogin = ss.userlogin
		inner join (select team_id from hr_team sk left join cc_target_group tg on sk.team_id = tg.pk_value and sk.org_id = tg.org_id
			where sk.org_id = ${def:org} and sk.skill_scope = ${fld:skill_scope}
			and tg.target_type=0 and concat(tg.target_year||'-'||tg.target_month)= (select to_char(('${def:date}'::date- ('1 month')::interval), 'yyyy-MM'))
		) team on team.team_id = ss.team_id
		where staff.org_id = ${def:org} 
		and staff.status=1) s on log.followby = s.userlogin
		where log.org_id = ${def:org}
		and log.custtype=1 and log.coMMcode is not null and log.coMMcode!=''
		and log.pk_value = cust.code
		and to_char(log.created, 'yyyy-MM')=to_char('${def:date}'::date, 'yyyy-MM')
		and c.operatortype=0
	)
	
union
--过期续费的会员
select 
	count(1) as huifang
from cc_customer cust
where cust.org_id = ${def:org} 
and not exists(
	select 1 from cc_card d where d.isgoon=0 and d.org_id = cust.org_id 
	and d.customercode = cust.code 
	and d.status !=0 and d.status!=6 
) 
and not exists(
	select 1 from cc_card d2 where 
	d2.customercode = cust.code  and d2.isgoon=1
) 
and exists(
		select 1 from cc_return_log  log 
		inner join cc_comm c on log.commcode=c.code and log.org_id = c.org_id
		inner join (select 
			staff.userlogin
		from hr_staff staff
		left join hr_team_staff ss on staff.userlogin = ss.userlogin
		inner join (select team_id from hr_team sk left join cc_target_group tg on sk.team_id = tg.pk_value and sk.org_id = tg.org_id
			where sk.org_id = ${def:org} and sk.skill_scope = ${fld:skill_scope}
			and tg.target_type=0 and concat(tg.target_year||'-'||tg.target_month)= (select to_char(('${def:date}'::date- ('1 month')::interval), 'yyyy-MM'))
		) team on team.team_id = ss.team_id
		where staff.org_id = ${def:org} 
		and staff.status=1) s on log.followby = s.userlogin
		where log.org_id = ${def:org}
		and log.custtype=1 and log.coMMcode is not null and log.coMMcode!=''
		and log.pk_value = cust.code
		and to_char(log.created, 'yyyy-MM')=to_char('${def:date}'::date, 'yyyy-MM')
		and c.operatortype=0
	)

union

select 
	count(1) as huifang
from cc_customer cust
where cust.org_id = ${def:org} 
and exists(
	select 1 from cc_card where status=1 and isgoon=0
	and cust.code = customercode and org_id = cust.org_id
)
and not exists(
	select 1 from cc_ptrest where org_id = cust.org_id
	and pttype!=5
	and cust.code =customercode
)
and exists(
		select 1 from cc_return_log  log 
		inner join cc_comm c on log.commcode=c.code and log.org_id = c.org_id
		inner join (select 
			staff.userlogin
		from hr_staff staff
		left join hr_team_staff ss on staff.userlogin = ss.userlogin
		inner join (select team_id from hr_team sk left join cc_target_group tg on sk.team_id = tg.pk_value and sk.org_id = tg.org_id
			where sk.org_id = ${def:org} and sk.skill_scope = ${fld:skill_scope}
			and tg.target_type=0 and concat(tg.target_year||'-'||tg.target_month)= (select to_char(('${def:date}'::date- ('1 month')::interval), 'yyyy-MM'))
		) team on team.team_id = ss.team_id
		where staff.org_id = ${def:org} 
		and staff.status=1) s on log.followby = s.userlogin
		where log.org_id = ${def:org}
		and log.custtype=1 and log.coMMcode is not null and log.coMMcode!=''
		and log.pk_value = cust.code
		and to_char(log.created, 'yyyy-MM')=to_char('${def:date}'::date, 'yyyy-MM')
		and c.operatortype=0
	)
) a
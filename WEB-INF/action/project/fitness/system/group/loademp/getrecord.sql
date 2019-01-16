select
	f.user_id,
    f.name
from hr_staff f 
left join  ${schema}s_user  u on  u.user_id=f.user_id
where 1=1 and f.org_id = ${def:org}
 AND f.is_member = 0 
  and exists (
	select 1 from hr_staff_skill fk
	inner join hr_skill k on fk.skill_id = k.skill_id
	WHERE fk.user_id = f.user_id  and k.org_id = ${def:org}
	and k.skill_scope    in (
	select regexp_split_to_table(${fld:group_type}, ',')
	)
)
and (case when ${fld:types}='1' 
	then f.user_id not in (select user_id::int from hr_team_staff) else 1=1 end)
AND  f.user_id <>'100' --超管隐藏zzn
order by f.user_id desc

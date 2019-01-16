select 
	t.skill_name,
	t.remark,
	t.skill_id,
	case skill_scope when '0' then '全国' when '1' then '区域' when '2' then '经销商' when '9' then '所有者' else '' end as scope_alias,
	p.tenantry_name
from	     
	hr_skill t
	inner join t_tenantry p on t.tenantry_id=p.tenantry_id
where 
	t.tenantry_id = ${def:tenantry}

	${filter}
	${orderby}
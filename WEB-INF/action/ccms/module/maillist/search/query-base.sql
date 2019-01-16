select 
userlogin   as col1
,case when  is_hidden='' then org_name  else mobile  end as col2
,case when  is_hidden='' then tt.org_id  else 0  end as col3
,is_hidden as col4
from(
select
org_id,org_name,show_order::varchar,org_path,'' as userlogin,'' as mobile,'' as is_hidden,'' as email
from 
hr_org
where 
org_path like ${fld:org_id}
and
	tenantry_id = ${def:tenantry}
union all
select
t1.org_id,t1.userlogin as org_name,t1.show_order::varchar,t3.org_path as org_path,t1.userlogin,mobile ,'none' as is_hidden,t1.email
from hr_staff t1 join hr_org t3
on  t1.org_id=t3.org_id
where 
'%'||t1.org_id||'%'=${fld:org_id}
) as tt
where 
	1=1

	${filter}
order by  is_hidden,org_path,show_order,userlogin,show_order,is_hidden desc



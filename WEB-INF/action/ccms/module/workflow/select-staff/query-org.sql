/*组织架构*/
select
	cast(t.org_id as varchar(4000)) as org_id
	,t.org_name
	,cast(t.pid as varchar(4000)) as pid
	,'0' as select_type
	,t.show_order
from 
	hr_org t
where 	
	t.tenantry_id = ${def:tenantry}
and
	exists(select 1 from hr_org o1,hr_org_post p1,hr_post_staff ps1  where o1.org_id = p1.org_id and p1.tuid = ps1.org_post_id and charindex(t.org_path,o1.org_path)>=1 and p1.tuid in ${lst:id@query-post.sql} and ps1.userlogin in ${lst:id@query-staff.sql})
union 
--岗位

select
	concat(cast(t.tuid as varchar(4000)),'_post') as org_id
	,t.org_post_name as org_name
	,cast(t.org_id as varchar(4000)) as pid
	,'0' as select_type
	,t.show_order
from
	hr_org_post t
	inner join hr_org o on t.org_id = o.org_id
where
	t.tuid in ${lst:id@query-post.sql}
union

--人员
select
	distinct
	concat(t.userlogin,';',cast(p.tuid as varchar(4000))) as org_id
	,concat(t.name,(case when (select count(*) from ${schema}s_session s where s.userlogin=t.userlogin and s.login_date='${def:date}')>0 then '(在线)' else '(未知)' end)) as org_name
	,concat(cast(p.tuid as varchar(4000)),'_post') as pid
	,'1' as select_type
	,t.show_order
from
	hr_staff t
	inner join hr_post_staff ps on t.userlogin = ps.userlogin 
	inner join hr_org_post p on ps.org_post_id = p.tuid
	inner join hr_org o on p.org_id = o.org_id
where
	ps.userlogin in ${lst:id@query-staff.sql}
	union
--无岗位人员
select
	distinct
	t.userlogin as org_id
	,concat(t.name,(case when (select count(*) from ${schema}s_session s where s.userlogin=t.userlogin and s.login_date='${def:date}')>0 then '(在线)' else '(未知)' end)) as org_name
	,cast(t.org_id as varchar(4000)) as pid
	,'1' as select_type
	,t.show_order
from
	hr_staff t
	inner join hr_org o on t.org_id = o.org_id and exists (select 1 from hr_org_post op where op.org_id = o.org_id and op.tuid in ${lst:id@query-post.sql})
where
	t.userlogin in ${lst:id@query-staff.sql}
and not exists
(select 1 from hr_post_staff ps where ps.userlogin=t.userlogin and ps.org_post_id in ${lst:id@query-post.sql})
order by show_order

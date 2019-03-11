select
	t.org_id,
	--COALESCE((case when t.org_id = f.org_id then null else t.pid end),0) as pid,
	pid,
	t.org_path,
	t.org_type,
	t.org_name ,
	case when get_txt_count(org_path,';') =2 then '' else  'none' end is_edit
from hr_org t
--left join hr_staff f on f.org_id = t.org_id and f.userlogin = '${def:user}'
where t.is_deleted = '0' 
--and	exists (
	--select 1 from hr_org os2 ,hr_staff s2 
	--where os2.org_id = s2.org_id and CHARINDEX(os2.org_path ,t.org_path) >= 1 and s2.userlogin = '${def:user}'

--)
order by t.show_order,t.org_name asc

select
	count(1) as num
from hr_staff
where user_id::varchar in (select regexp_split_to_table(${fld:user_id},',') from dual)
and org_id = ${def:org} and status != ${fld:status}

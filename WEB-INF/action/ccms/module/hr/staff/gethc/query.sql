select
hc_id,
hc_name
from hr_headcount
where
org_post_id=${fld:org_post_id}
and
not
exists(
	select 1 from hr_staff where hr_headcount.hc_id=hr_staff.hc_id
)
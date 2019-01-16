select
	t.tuid as  form_id,
	t.form_name as  form_name,
	t.form_type,
	case t.form_type when '0' then '业务类型' when '1' then '工单类型' when '2' then '活动类型' when '9' then '系统类型' end as  type_alias
from
	t_form t
where
	t.pid is null
and
	t.form_type in ('0','1') --基本,流程
order by 
	t.form_type,t.form_name
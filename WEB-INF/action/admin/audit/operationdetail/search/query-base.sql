select 
	(select f.field_name from t_table t left join t_field f on f.table_id=t.tuid where t.table_code=${fld:table_code} and f.field_code=tdl.field_code limit 1) as field_name,
	s.name,
	tdl.created,
	tdl.before_value,
	tdl.after_value
	
from
	t_table_data_log tdl
	left join hr_staff s
	on tdl.createdby=s.userlogin
	
where
	1=1
	
${filter}
${orderby}


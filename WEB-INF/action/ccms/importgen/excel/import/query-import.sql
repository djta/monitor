select
	f.pre_class_name
	,f.post_class_name
	,f.before_class_name
	,after_sql
from
	t_import f
where
	f.tuid = ${fld:imp_id}
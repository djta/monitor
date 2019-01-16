select
case when title_line_num is null then 1 else title_line_num end as title_line_num
	,validator_class_name
	,is_error_continue
from
	t_import 
where
	tuid = ${fld:imp_id}
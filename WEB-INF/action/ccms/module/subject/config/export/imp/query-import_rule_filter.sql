select
	f.*
from 
	t_import_table t
	inner join t_import_rule d
	on t.tuid = d.tab_id
	inner join t_import_rule_filter f
	on f.rule_id = d.tuid
where 
	t.imp_id = ${fld:imp_id}
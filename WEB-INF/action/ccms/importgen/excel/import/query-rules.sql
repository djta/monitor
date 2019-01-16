select
	b.tuid as rule_id
	,b.filter_type
from
	t_import_rule b
where
	b.tab_id = ${tab_id}
order by
	b.rule_order
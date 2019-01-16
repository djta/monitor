select
rule_name
from
t_import_rule
where rule_name = ${fld:rule_name}
and tab_id = ${fld:tab_id}
select
	d.*
from 
	t_import_table t
	inner join t_import_field d
	on t.tuid = d.tab_id
where 
	t.imp_id = ${fld:imp_id} and 
	d.col_name not in (select bpk_field_alias from t_import_table where t_import_table.imp_id=${fld:imp_id}) 

ORDER BY show_order
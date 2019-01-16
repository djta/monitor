select
	h.tuid
	,h.history_id
	,tab_id
	,b.tab_name
	,h.total_record
	,h.insert_record
	,h.update_record
	,h.error_count
from
	t_import_table_history h
	inner join t_import_table b
	on h.tab_id = b.tuid
where
	h.history_id = ${fld:history_id_filter}

${filter}
${orderby}
and
	(select max(table_name) as table_name from t_table where tuid=t_import_table.table_id ) like concat('%',${fld:table_name_filter},'%')

and
	lower(t.table_code) = (select lower(table_code) from t_table where tuid=${fld:table_id})
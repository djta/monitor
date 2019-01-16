select
	t.field_code
	,t.after_value as value
from
	t_table_data_log t
	inner join (
		SELECT
			max(tuid) as tuid
		FROM
			t_table_data_log
		WHERE
			table_code = '${table}'
		and
			pk_value = ${fld:__pk_value__}
		and
			snapshot <= ${fld:snapshot}
		group by
			field_code
		) a
	on t.tuid = a.tuid
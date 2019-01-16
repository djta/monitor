SELECT
	fi.field_code_alias
FROM
	t_report f
	inner join t_report_filter_field ff
	on ff.report_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
WHERE
	f.tuid = ${fld:report_id}
and
case when ff.show_type is null then fi.show_type else ff.show_type end = '2'	--多选框
order by 
	ff.show_order
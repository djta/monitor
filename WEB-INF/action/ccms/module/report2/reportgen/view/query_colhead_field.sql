SELECT
    lower(fi.field_code_alias)	as	field_code
    ,ff.show_order
    ,replace(ff.head_name,'${LBL','${lbl') as head_name
FROM
	t_report f
	inner join t_report_show_field ff
	on ff.report_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
WHERE
    f.tuid = ${fld:report_id}
and
    ff.is_col_head = '1'
order by
	ff.show_order
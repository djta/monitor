SELECT
    lower(fi.field_code)  as	field_code
FROM
	t_report f
	left join t_report_show_field ff
	on ff.report_id = f.tuid
	left join t_field fi
	on ff.field_id = fi.tuid
WHERE
    f.tuid = ${fld:report_id}
and
    ff.is_order_by = '1'
AND
	(ff.is_row_head = '1' or ff.is_col_head = '1') --选中了行头或列头
    
order by
    ff.show_order
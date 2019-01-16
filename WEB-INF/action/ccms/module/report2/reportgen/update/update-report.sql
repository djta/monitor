UPDATE 	t_report_show_field
SET
	show_order = ${fld:report_order}
	,is_row_head = ${fld:is_row_head}
	,is_col_head = ${fld:is_col_head}
--	,is_cross_value = ${fld:is_cross_value}
WHERE
	tuid = ${fld:field_report}

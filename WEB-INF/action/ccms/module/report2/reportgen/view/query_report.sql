SELECT
    report_type
    ,report_name_${def:locale} as report_name
    ,show_total_type
    ,show_sub_total_type
    ,show_percent_type
    ,if_customer_design
    ,is_merge_zero
    ,is_merge_vertical
    ,is_show_zero
    ,'${def:locale}' as locale
FROM
	t_report f
WHERE
    f.tuid = ${fld:report_id}

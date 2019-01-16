SELECT
	case when report_type ='2' then '' else 'none' end as show_row_cross
	,case when if_customer_design='0' then 'none' else '' end as if_customer_config
	,report_name_${def:locale} as report_name
	,jor_file_name
	,congnos_ip
FROM
	t_report f
WHERE
    f.tuid = ${fld:report_id}

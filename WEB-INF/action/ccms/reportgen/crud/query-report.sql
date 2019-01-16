SELECT
	f.url
	,replace(replace(f.form_js,'${DEF','${def'),'${LBL','${lbl') as form_js
	,replace(replace(f.loadfilter_js,'${DEF','${def'),'${LBL','${lbl') as loadfilter_js
	,replace(replace(f.searchpre_js,'${DEF','${def'),'${LBL','${lbl') as searchpre_js
	,replace(replace(f.searchback_js,'${DEF','${def'),'${LBL','${lbl') as searchback_js
	,f.engine_type
	,f.report_name_${def:locale} as report_name
	,case when f.report_type ='2' then '' else 'none' end as show_row_cross
	,case when f.if_customer_design='0' then 'none' else '' end as if_customer_config
	,f.jor_file_name
	,f.filter_showtype
FROM
	t_report f
WHERE
	f.tuid = ${fld:report_id}
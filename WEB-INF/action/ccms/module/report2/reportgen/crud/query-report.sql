SELECT
	f.url
	,replace(replace(f.form_js,'${DEF','${def'),'${LBL','${lbl') as form_js
	,replace(replace(f.loadfilter_js,'${DEF','${def'),'${LBL','${lbl') as loadfilter_js
	,replace(replace(f.searchpre_js,'${DEF','${def'),'${LBL','${lbl') as searchpre_js
	,replace(replace(f.searchback_js,'${DEF','${def'),'${LBL','${lbl') as searchback_js
	,f.engine_type
	,report_name_${def:locale} as report_name
FROM
	t_report f
WHERE
	f.tuid = ${fld:report_id}
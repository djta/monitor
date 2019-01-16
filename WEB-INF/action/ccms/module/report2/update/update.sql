UPDATE
	t_report
SET
	subject_id     =${fld:subject_id}
	,table_id     =${fld:table_id}
	,report_name_cn     =${fld:report_name_cn}
	,report_name_en     =${fld:report_name_en}
	,report_type     =${fld:report_type}
	,show_total_type     =${fld:show_total_type}
	,show_sub_total_type     =${fld:show_sub_total_type}
	,show_percent_type     =${fld:show_percent_type}
	,if_customer_design     =${fld:if_customer_design}
	,is_sql_phrase     =${fld:is_sql_phrase}
	,report_sql     =${fld:report_sql}
	,col_num_filter     =${fld:col_num_filter}
	,remark	 =${fld:remark}
	,url = ${fld:url}
	,is_merge_zero = ${fld:is_merge_zero}
	,is_merge_vertical = ${fld:is_merge_vertical}
	,is_show_zero = ${fld:is_show_zero}
	,form_js = ${fld:form_js}
	,loadfilter_js = ${fld:loadfilter_js}
	,searchpre_js = ${fld:searchpre_js}
	,searchback_js = ${fld:searchback_js}
	,engine_type = ${fld:engine_type}
	,jor_file_name = ${fld:jor_file_name}
	,congnos_ip = ${fld:congnos_ip}
WHERE
	tuid	=${fld:tuid}

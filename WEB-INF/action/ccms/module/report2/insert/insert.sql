INSERT	INTO
t_report
(
	tuid
	, subject_id
	, table_id
	, report_name_cn
	, report_name_en
	, report_type
	, show_total_type
	, show_sub_total_type
	, show_percent_type
	, if_customer_design
	, is_sql_phrase
	, report_sql
	, col_num_filter
	, remark
	, url
	,is_merge_zero
	,is_merge_vertical
	,is_show_zero
	, form_js
	, loadfilter_js
	, searchpre_js
	, searchback_js	
	, engine_type
	, jor_file_name
	, congnos_ip
)
VALUES
(
	${seq:nextval@seq_report}
	,${fld:subject_id}
	,${fld:table_id}
	,${fld:report_name_cn}
	,${fld:report_name_en}
	,${fld:report_type}
	,${fld:show_total_type}
	,${fld:show_sub_total_type}
	,${fld:show_percent_type}
	,${fld:if_customer_design}
	,${fld:is_sql_phrase}
	,${fld:report_sql}
	,${fld:col_num_filter}
	,${fld:remark}
	,${fld:url}
	,${fld:is_merge_zero}
	,${fld:is_merge_vertical}
	,${fld:is_show_zero}
	,${fld:form_js}
	,${fld:loadfilter_js}
	,${fld:searchpre_js}
	,${fld:searchback_js}
	,${fld:engine_type}
	,${fld:jor_file_name} 
	,${fld:congnos_ip} 
	
)
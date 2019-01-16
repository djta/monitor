INSERT	INTO
t_report
(
	tuid, subject_id, table_id, report_name_cn, report_name_en, report_type, deleted, 
       enabled, remark, created, createdby, updated, updatedby, version, 
       col_num_filter, report_sql, show_total_type, show_sub_total_type, 
       show_percent_type, if_customer_design, domain_value_type, domain_value_show_type, 
       is_sql_phrase, url, is_merge_zero, is_show_zero, form_js, loadfilter_js, searchback_js, is_merge_vertical, engine_type, jor_file_name, searchpre_js, document_id, is_drill, group_type
)
select
	${report_id}
	,subject_id
	,table_id, ${fld:report_name_cn}, ${fld:report_name_cn}, report_type, deleted, 
       enabled, remark, created, createdby, updated, updatedby, version, 
       col_num_filter, report_sql, show_total_type, show_sub_total_type, 
       show_percent_type, if_customer_design, domain_value_type, domain_value_show_type, 
       is_sql_phrase, url, is_merge_zero, is_show_zero, form_js, loadfilter_js ,searchback_js, is_merge_vertical, engine_type, jor_file_name, searchpre_js, document_id, is_drill, group_type
from
	t_report
where
	tuid = ${fld:report_id}
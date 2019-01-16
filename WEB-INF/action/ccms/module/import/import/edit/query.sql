SELECT
tuid as id
,subject_id
,imp_name
,before_class_name
,pre_class_name
,post_class_name
,validator_class_name
,title_line_num
,case when is_previwe_flag='1' then  is_previwe_flag
			else '0' end as is_previwe_flag
,remark
,after_sql
,is_error_continue
FROM
	t_import p
WHERE
	p.tuid=${fld:id}
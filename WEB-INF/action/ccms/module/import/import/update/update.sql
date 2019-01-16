UPDATE
	t_import
SET
  imp_name = ${fld:imp_name}  
  ,before_class_name=${fld:before_class_name}
  ,pre_class_name = ${fld:pre_class_name}
  ,post_class_name = ${fld:post_class_name}
  ,title_line_num = ${fld:title_line_num}
  ,is_previwe_flag = ${fld:is_previwe_flag}
  ,remark = ${fld:remark}
  ,after_sql = ${fld:after_sql}
  ,updated = {ts '${def:timestamp}'}
  ,updatedby = '${def:user}'
  ,validator_class_name = ${fld:validator_class_name}
  ,is_error_continue = ${fld:is_error_continue}
WHERE
	tuid = ${fld:tuid}
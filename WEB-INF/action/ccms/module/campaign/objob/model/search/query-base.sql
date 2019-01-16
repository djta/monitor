SELECT
    t.tuid
    , t.table_id
    , t.form_id
    , t.search_table_id
    , t.search_form_id
    , t.model_name
    , case when t.is_enabled = '0' then '运行'
    		when t.is_enabled = '1' then '停止'
      end as is_enabled
       , case when t.is_enabled = '0' then concat('<a href="javascript:void(0);" onclick="javascript:enabled(' , t.tuid , ',1);" title="停止">停止</a>')
    		when t.is_enabled = '1' then concat('<a href="javascript:void(0);" onclick="javascript:enabled(' , t.tuid , ',0);" title="运行">运行</a>')
      end as url_enabled
FROM
	cs_job_model t
WHERE
    1 = 1
${filter}

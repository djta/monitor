SELECT
    t.tuid
    , m.model_name
    , e.template_name
    , t.table_id
    , t.form_id
    , t.search_table_id
    , t.search_form_id
    , t.job_name
    , t.from_date
    , t.to_date
    , case when t.is_enabled = '0' then '停止'
    		when t.is_enabled = '1' then '运行'
      end as is_enabled
    , t.reserve_accuracy
    , case when t.is_enabled = '0' then concat('<a href="javascript:void(0);" onclick="javascript:setJobStatus(' , t.tuid , ',1);" title="启用">启用</a>')
    		when t.is_enabled = '1' then concat('<a href="javascript:void(0);" onclick="javascript:setJobStatus(' , t.tuid , ',0);" title="停止">停止</a>')
      end as url_enabled
    , case when t.parent_id is null and t.is_enabled = '1' and t.if_manual_push_flag='1' then concat('<a href="javascript:void(0);" onclick="javascript:showPool(',t.search_form_id,',',t.search_table_id,',',t.tuid,');" title="生成数据">传输到CS</a>')
    		else  ''
      end as show_pool
    , case when t.parent_id is null then concat('<a href="javascript:void(0);" onclick="javascript:showDataLog(',t.tuid,');" title="数据注入日志">注入日志</a>')
    		else  ''
      end as show_pool_log
FROM
	cs_job t
	left join cs_job_model m on t.model_id = m.tuid
	left join cs_job_template e on e.tuid=t.template_id 
WHERE
    1 = 1
${filter}

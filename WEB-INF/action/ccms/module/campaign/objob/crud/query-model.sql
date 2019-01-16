SELECT
    t.tuid  as model_id
    , t.model_name
FROM
	cs_job_model t 
where 
    t.is_enabled='0'

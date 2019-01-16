SELECT
	distinct 
    t.tuid  as job_id
    , t.template_name
FROM
	cs_job_template t
where
	 subject_id = ${def:subject}
	and 
    t.is_enabled='0'
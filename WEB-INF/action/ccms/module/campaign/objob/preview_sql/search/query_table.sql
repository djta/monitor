SELECT
    t.table_code
    ,j.cust_code as cust_code_field
FROM
	cs_job j
	left join cs_job_model m on j.model_id = m.tuid
	left join t_form f on m.search_form_id = f.tuid
	left join t_table t on f.table_id=t.tuid
WHERE
	j.tuid = ${fld:job_id}

SELECT
    t.table_code
    ,j.cust_code as cust_code_field
FROM
	cs_job j
	left join t_form f 	on j.search_form_id = f.tuid
	left join t_table t on f.table_id=t.tuid
WHERE
	f.tuid = ${fld:form_id}
and
	j.tuid = ${fld:job_id}

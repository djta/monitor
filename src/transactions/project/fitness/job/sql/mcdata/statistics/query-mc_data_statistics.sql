SELECT 
	count(1) as num
FROM cc_mc_data_statistics 
WHERE userlogin = ${fld:userlogin}
	and to_char(concat(year, '-', month, '-01')::date,'YYYYMM')=
		to_char('${def:date}'::date - interval '1 month','YYYYMM')
and org_id = ${fld:org_id}
	
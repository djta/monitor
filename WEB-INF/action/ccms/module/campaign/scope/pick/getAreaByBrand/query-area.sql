SELECT
	t1.domain_text_${def:locale} as area_name,
	t1.domain_value as area_code
FROM
	cc_dealer d
	inner join t_domain t1 on t1.namespace='IntendedBrand' and d.area=t1.domain_value
WHERE
	d.area = ${fld:brand}
 and
	d.status = '1'
and
	d.subject_id=${def:subject}
order by 
	t1.domain_text_${def:locale}

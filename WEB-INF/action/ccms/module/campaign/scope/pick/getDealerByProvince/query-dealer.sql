SELECT
	d.dealer_name_${def:locale} as dealer_name,
	d.dealer_code
FROM
	cc_dealer d
WHERE
	d.province = ${fld:province}
and
	d.dealer_code = ${fld:brand}
and
	d.status = '1'
and
	d.subject_id=${def:subject}
order by 
	d.dealer_name_${def:locale}

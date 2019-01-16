UPDATE
	hr_org_info
SET
	address = ${fld:address}
	,contacts = ${fld:contacts}
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}'
	,contact_phone = ${fld:contact_phone}
	,province = ${fld:province}
	,category_label = ${fld:category_label}
	,city = ${fld:city}
	,featured_products = ${fld:featured_products}
	,features = ${fld:features}
	,business_hours = ${fld:business_hours}
WHERE
	tuid = ${fld:tuid_m}
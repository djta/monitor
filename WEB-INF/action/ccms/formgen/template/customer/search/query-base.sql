select 
	cust_code,
	cust_name,
	replace(mobile, substring(mobile, 4, 4), '****') as mobile
from cc_customer 
where cust_code not in (${fld:cust_code1})
	
	${filter}
	${orderby}
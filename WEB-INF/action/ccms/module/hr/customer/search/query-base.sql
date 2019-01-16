select 
	cust_code,
	cust_name,
	replace(mobile, substring(mobile, 4, 4), '****') as mobile
from cc_customer 
where 1=1
	
--以下sql无需更改
	${filter}
	${orderby}
select 
  province,
	city 
from 
	cc_customer cc 
where 
	cc.cust_code=${fld:cust_code}   
 
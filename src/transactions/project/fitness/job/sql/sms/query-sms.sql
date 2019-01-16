select 
	s.tuid,
	s.cust_code,
	c.name,
	--s.receiver,
	'18611755240' as receiver,
	s.content,
	t.template_content 
from cc_sms s 
inner join cc_sms_template t on s.template_id = t.tuid 
left join cc_customer c on s.cust_code = c.code 
where s.account_id = ${fld:account_id} 
and s.status = '0' and s.msg_type = '0' 
and s.receiver is not null and s.receiver != '' 
order by created limit 1000

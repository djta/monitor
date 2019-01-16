insert into cc_message(
	tuid,
	senduser,
	recuser,
	recusername,
	content,
	sendtime,
	org_id
)
select 
	${seq:nextval@seq_cc_message},
	NULL,
	customercode,
	(
		select c.name from cc_customer c 
		where c.code = d.customercode and c.org_id = ${fld:org_id}
	),
	concat('停卡到期，自动开卡，会员卡有效期延长', '${def:date}'::date-startdate::date, '天'),
	{ts'${def:timestamp}'},
	org_id 
from cc_savestopcard d 
where code = ${fld:code} 
and org_id = ${fld:org_id}

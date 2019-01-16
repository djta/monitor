insert into cc_operatelog(
	code,
	org_id,
	opertype,
	relatedetail,
	factmoney,
	createdate,
	createtime,
	createdby,
	remark,
	status
)
select 
	${seq:nextval@seq_cc_operatelog},
	org_id,
	'47',
	concat(cardcode, ';', customercode),
	money,
	'${def:date}',
	'${def:time}',
	'${def:user}',
	remark,
	1 
from cc_savestopcard
where code = ${fld:code} 
and org_id = ${fld:org_id}

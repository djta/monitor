select
	t.content as body,
	t.tuid as template_id,
	'' as account_id,
	subject_name
from
	cc_remind_template t
where
	tuid = ${tuid}
select
	t.template_content as body,
	t.account_id
from
	cc_sms_template t
where
	tuid = ${tuid}
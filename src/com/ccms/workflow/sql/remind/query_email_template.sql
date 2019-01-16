select
	t.content as body,
	t.account_id
from
	cc_email_template t
where
	tuid = ${tuid}
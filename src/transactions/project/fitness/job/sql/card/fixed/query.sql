select
	card.code,
	card.org_id
from cc_card card
left join cc_cardtype ct on card.cardtype = ct.code and card.org_id = ct.org_id
where card.status=2 and card.isgoon = 0 and card.starttype=1
and '${def:date}'::date>(card.created::date+ct.opencarddeadline)

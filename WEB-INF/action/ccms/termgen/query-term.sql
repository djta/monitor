select
	t.term_name,
	t.tuid as term_id,
	to_char({ts '${def:timestamp}'},'yyyy-MM-dd hh24:mi:ss') as start_time,
	t.pre_class,
	t.post_class,
	t.introduction,
	case when t.introduction is not null then '1' else '0' end as has_begin,
	t.ending,
	case when t.ending is not null then '1' else '0' end as has_ending,
	t.question_bank_id
from
	t_term t
where
	t.tuid = ${fld:term_id}
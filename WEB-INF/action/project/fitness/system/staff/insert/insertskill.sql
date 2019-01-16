insert into hr_staff_skill
(
  tuid,
  user_id,
  userlogin,
  skill_id,
  isclass,
  created,
  createdby
)
values
(
  ${seq:nextval@seq_hr_staff_skill},
  ${seq:currval@${schema}seq_user},
  ${fld:userlogin},
  ${fld:_skill_ids},
  (case when ${fld:isclass} is null then 0 else ${fld:isclass} end),
	{ts '${def:timestamp}'},
	'${def:user}'
)
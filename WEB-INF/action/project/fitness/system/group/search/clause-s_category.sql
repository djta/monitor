and
t.skill_scope in
(select regexp_split_to_table(${fld:s_category}, ','))

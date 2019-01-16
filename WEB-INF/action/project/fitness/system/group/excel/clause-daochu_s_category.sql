and
t.skill_scope in
(select regexp_split_to_table(${fld:daochu_s_category}, ';'))

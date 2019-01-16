SELECT
	concat('${bpk_field_prefix}', ${seq:nextval@${bpk_field_seq}}) as  colname_mark
FROM
	dual
select
	concat('${bpk_field_prefix}',${seq:nextval@${bpk_field_seq}}) as tuid
from
	dual
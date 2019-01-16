select
	t.tuid as old_report_id
	,${seq:nextval@seq_report} as report_id
from
	t_report t
where
	t.subject_id = ${fld:subject_id}
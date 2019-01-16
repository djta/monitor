INSERT	INTO
t_subject
(
	tuid,
	subject_name
	,is_deleted
	,remark
	,created
	,createdby
	,updated
	,updatedby
)
select
	${seq:nextval@seq_subject}
	,concat(${fld:subject_name},' - Copy')
	,is_deleted
	,remark
	,created
	,createdby
	,updated
	,updatedby
from
	t_subject
where
	tuid = ${fld:subject_id}
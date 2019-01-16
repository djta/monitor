INSERT	INTO
t_subject
(
	tuid
	,subject_name
	,remark
	,is_deleted
	,created
	,createdby
)
VALUES
(
	${seq:nextval@seq_subject}
	,${fld:subject_name}
	,${fld:remark}
	,'0'
	,{ts '${def:timestamp}'}
	,'${def:user}'
)
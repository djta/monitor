INSERT	INTO
	t_term
(
	tuid
	,term_name
	,term_type
	,pre_class
	,post_class
	,remark
	,status
	,tenantry_id
	,logo_path
	,introduction
	,ending
	,question_bank_id
	,subject_id
)
VALUES
(
	${seq:nextval@seq_term}
	,${fld:term_name}
	,${fld:term_type}
	,${fld:pre_class} 
	,${fld:post_class}
	,${fld:remark}
	,${fld:status}
	,${def:tenantry}
	,${fld:logo_path}
	,${fld:introduction}
	,${fld:ending}
	,${fld:question_bank_id}
	,${def:subject}
)
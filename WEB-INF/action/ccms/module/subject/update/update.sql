UPDATE
	t_subject
SET
	subject_name     =${fld:subject_name}
	,remark		 =${fld:remark}
	,updated      ={ts '${def:timestamp}'}
	,updatedby	= '${def:user}'
WHERE
	tuid	=${fld:tuid}

SELECT
    a.tuid
    , a.document_name
    , case a.action_type when '0' then '表单' when '3' then '报表' else '' end as action_type
    , a.created
    , a.remark
    , a.subject_id
    , t.table_name
    , f.form_name	
    , f.tuid as form_id
    , t.tuid as table_id
    , a.document_type
FROM
	t_document a
	left join t_subject s on a.subject_id = s.tuid
	left join t_table t on a.table_id = t.tuid
	left join t_form f on a.form_id = f.tuid
WHERE
	a.is_deleted = '0'

${filter}
${orderby}


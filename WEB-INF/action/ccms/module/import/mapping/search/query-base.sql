SELECT
    t.tuid
    ,t.old_data
    ,(select domain_text_${def:locale} as domain_text from t_domain where domain_value=t.mapping_value and namespace=t.namespace_mapping) as mapping_text
    ,t.namespace_mapping
FROM
	t_import_mapping t
WHERE
	t.subject_id = ${def:subject}

${filter}

order by t.namespace_mapping
SELECT
    fi.field_code_alias as field_code
    , fi.field_name_${def:locale}  as  field_name
FROM
	t_document d
	inner join t_form f on d.form_id = f.tuid
	inner join T_FORM_FILTER_FIELD fs on f.tuid = fs.form_id
	inner join t_field fi on fs.field_id = fi.tuid
WHERE
    d.tuid = ${fld:document_id}
union
SELECT
    fi.field_code_alias as field_code
    , fi.field_name_${def:locale}  as  field_name
FROM
	t_document d
	inner join t_report_filter_field fs on d.report_id = fs.report_id
	inner join t_field fi on fs.field_id = fi.tuid
WHERE
    d.tuid = ${fld:document_id}
UNION
SELECT
    fi.field_code_alias as field_code
    , fi.field_name_${def:locale}  as  field_name
FROM
	t_document d
	inner join t_form f on d.form_id = f.tuid
	inner join T_FORM_SHOW_FIELD fs on f.tuid = fs.form_id
	inner join t_field fi on fs.field_id = fi.tuid
WHERE
    d.tuid = ${fld:document_id}
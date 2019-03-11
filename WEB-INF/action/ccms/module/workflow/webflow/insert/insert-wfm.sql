INSERT	INTO
os_wfm
(
	tuid


	, wfm_name

	, xml_value
	, xml_release
	,created
	,createdby
)
VALUES
(
	${fld:wfm_id}

	,${fld:wfm_id}
	,?
	,?
	,{ts '${def:timestamp}'}
	,'${def:user}'
)
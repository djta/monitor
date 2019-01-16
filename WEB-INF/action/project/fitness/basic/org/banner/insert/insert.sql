INSERT INTO hr_org_banner(
	tuid, 
	bannername, 
	attachid, 
	linkurl, 
	showorder,
	status, 
	org_id,
	createdby, 
	created,
	bannertype
) VALUES(
	${seq:nextval@seq_hr_org_banner},
	${fld:bannername},
	${fld:upload_id},
	${fld:linkurl},
	${fld:showorder},
	1,
	COALESCE(${fld:org_id1}, ${def:org}),
	'${def:user}',
	{ts '${def:timestamp}'},
	10
)

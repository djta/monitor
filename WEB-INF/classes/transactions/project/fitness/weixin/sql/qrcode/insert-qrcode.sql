INSERT INTO wx_qrcode(
	tuid,
	qrcodetype,
	expire_seconds,
	qrcode_uri,
	pk_value,
	data_type,
	status,
	remark,
	createdby,
	created,
	org_id,
	resultcode
) VALUES(
	${tuid},
	${qrcodetype},
	${expire_seconds},
	'${qrcode_uri}',
	${fld:pk_value},
	${fld:data_type},
	1,
	NULL,
	'${def:user}',
	{ts '${def:timestamp}'},
	${def:org},
	0
);
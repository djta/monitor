SELECT
	ff.tuid as field_report
	,ff.is_col_head
	,ff.is_row_head
	,ff.is_cross_value
	,lower(fi.field_code_alias)	as	field_code
	,concat(lower(fi.field_code_alias),'_rawdata') as field_code_rawdata
	,ff.show_order
	,replace(ff.head_name,'${LBL','${lbl') as head_name
	,case when f.is_drill='1' and ff.document_id is not null then concat(concat(concat('${def:context}/action/doc?document_id=',ff.document_id),${fld:filter}),'<filter-params>&${FLD:parameter}=${FLD:value}</filter-params>') else '' end as url
	,ff.cal_type_show
	,fi.field_type
	,ff.format
FROM
	t_report f
	inner join t_report_show_field ff
	on ff.report_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
WHERE
	f.tuid = ${fld:report_id}
order by
	ff.show_order
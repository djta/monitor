SELECT
	lower(fi.field_code) as	field_code
	,lower(fi.field_code_alias) as colname
	,case when fi.is_virtual_type='1' and ff.print_html='1' 
			then replace(replace(replace(replace(fi.field_code,'${DEF','${def'),'${LBL','${lbl'),'${FLD','${fld'),'''','')
		else
			concat(
				concat(lower('${FLD:'),lower(fi.field_code_alias)),
				(case 
					when ff.format is not null and ff.format !='' 
						then concat(concat('@' , ff.format) , '}') 
					when lower(fi.field_type)='date' then '@yyyy-MM-dd}'
					when lower(fi.field_type)='timestamp' then '@yyyy-MM-dd HH:mm:ss}' 
					when lower(fi.field_type)='varchar' then '@js}'
					else '}' 
					end
				)
			) 
	end as colname_mark
	,fi.field_name_cn  as  field_name
	,fi.field_name_cn  as  field_name_cn
	,fi.field_name_en  as  field_name_en
	,fi.field_type
	,ff.show_order
	,'' as is_nowrap
	,case when ff.sort_order = '0' then 'class="sortable"' else '' end as is_sort
	,case when ff.sort_order = '0' then '.' else '' end as is_sort_alias
	,case when ff.show_align is null then 'left' else ff.show_align end as show_align
	,ff.show_flag
FROM
	t_form f
	inner join t_form_grid_field ff
	on ff.form_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
WHERE
	f.tuid = ${form_id}
order by 
	show_order
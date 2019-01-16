SELECT
    lower(fi.field_code)	as	field_code
    ,concat(lower('${FLD:'),lower(fi.field_code_alias),'}')	as  colname_mark
    ,fi.field_code_alias
    ,fi.field_name_${def:locale}  as  field_name
	,nvl(ff.show_type,fi.show_type) as show_type
    ,case when nvl(ff.show_type,fi.show_type)='0' then ff.width when nvl(ff.show_type,fi.show_type)='1' and ff.width=0 then 180 else ff.width end as show_width									/*显示宽度*/
    ,fi.field_length	as	maxlength		/*最大长度*/
    ,fi.tuid		as		field_id  
    ,f.col_num_filter	as	col_num			/*查询界面显示列数*/
    ,'formFilter'		as	pickup_form		/*pickup控件参数*/
    ,fi.tuid		as	pickup_field		/*pickup控件参数*/
    ,fi.is_mandatory		as	is_required	/*是否必填项*/
	  ,case when fi.is_mandatory='0' then ' required' else '' end as is_required_alias
    ,fi.format_mark			/*显示格式*/
    ,nvl(lower(fi.default_value),'')   as  default_value
    ,nvl(fi.domain_namespace,'null')  as  domain_namespace
    ,case when nvl(ff.show_type,fi.show_type)='8' then  replace(replace(nvl(fi.plugin_control,''),'${LBL','${lbl'),'${DEF','${def') else '' end as  plugin_control
    ,nvl(ff.filter_type,'=') as filter_type
    ,ff.filter_value
    ,replace(nvl(fi.plugin_code,''),'${LBL','${lbl') as  plugin_code
FROM
	t_report f
	inner join t_report_filter_field ff
	on ff.report_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
WHERE
    f.tuid = ${fld:report_id}
order by 
	ff.show_order
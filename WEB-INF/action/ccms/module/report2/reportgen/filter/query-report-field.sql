select 
	sf.tuid    as  id
	, t.field_name_${def:locale} alias

	, case
	    when (select count(*) from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)=0 then ''
	    else 'checked'
	  end   as  checked1

	,case 
	    when (select a.show_order from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid) is null then 0
	    else  (select a.show_order from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)
	 end   as  report_order

	,(select a.cal_type from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)   as  cal_type
	,(select a.cal_type_show from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)   as  cal_type_show
	,(select a.format from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)   as  report_format
	,case 
	    when (select a.width from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid) is null then 0
	    else  (select a.width from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)
	 end   as  report_width
	,(select a.is_group_by from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)   as  is_group_by
	,(select a.is_axis_x from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)   as  is_axis_x
	,(select a.is_axis_y from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)   as  is_axis_y
	,(select a.series_y from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)   as  series_y
	,(select a.is_row_head from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)   as  is_row_head
	,(select a.is_col_head from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)   as  is_col_head
	,(select a.is_cross_value from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)   as  is_cross_value
	,(select a.head_name from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)   as  head_name
	,(select a.is_order_by from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid)   as  is_order_by

	, case
	    when (select count(*) from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid  and a.is_group_by = '1')=0 then ''
	    else 'checked'
	  end   as  checked3

	, case
	    when (select count(*) from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid  and a.is_axis_x = '1')=0 then ''
	    else 'checked'
	  end   as  checked7
	, case
	    when (select count(*) from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid  and a.is_axis_y = '1')=0 then ''
	    else 'checked'
	  end   as  checked8
	, case
	    when (select count(*) from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid  and a.is_row_head = '1')=0 then ''
	    else 'checked'
	  end   as  checked9
	, case
	    when (select count(*) from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid  and a.is_col_head = '1')=0 then ''
	    else 'checked'
	  end   as  checked10
	, case
	    when (select count(*) from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid  and a.is_cross_value = '1')=0 then ''
	    else 'checked'
	  end   as  checked11
	, case
	    when (select count(*) from t_report_show_field a where a.report_id = ${fld:report_id} and a.field_id = t.tuid  and a.is_order_by = '1')=0 then ''
	    else 'checked'
	  end   as  checked12

from 
	t_report r
    inner join t_report_show_field sf
    on sf.report_id=r.tuid
    inner join t_field t
    on sf.field_id = t.tuid 
where 
    r.tuid = ${fld:report_id}    
AND
	sf.is_cross_value = '0' --选中了行头或列头
order by 
	sf.show_order

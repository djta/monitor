SELECT
    case 
        when (select count(*) from t_domain d where d.is_enabled='1' and lower(d.namespace)=lower(fi.domain_namespace))>0 
        then concat('(select max(d.domain_text_${def:locale}) from t_domain d where d.is_enabled=''1'' and d.namespace=''',fi.domain_namespace,''' and d.domain_value = ',t.table_code,'.',fi.field_code,' )') 
        when fi.fk_tab is not null
        then concat('(select ',fi.fk_fld_anchor,' from ',fi.fk_tab,' a where a.',fi.fk_fld_id,'= ',t.table_code,'.',fi.field_code,')')
        else concat(t.table_code , '.' , lower(fi.field_code))  
        end  as	field_code
    ,lower(fi.field_code_alias) as colname
    ,concat(lower('${FLD:'),lower(fi.field_code_alias),'}')	as  colname_mark
    ,fi.field_name_${def:locale}  as  field_name
FROM
	t_form f
	left join t_form_grid_field ff
	on ff.form_id = f.tuid
	left join t_field fi
	on ff.field_id = fi.tuid
	left join t_table t
	on f.table_id = t.tuid
WHERE
    f.tuid = ${fld:form_id}

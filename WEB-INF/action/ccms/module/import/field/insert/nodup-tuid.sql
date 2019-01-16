select bpk_field from t_table
where tuid = (select table_id from t_import_table where tuid=${fld:tab_id})
and bpk_field = ${fld:field_code}
and '0' != ${fld:update_mode}
and ${fld:update_mode} is not null
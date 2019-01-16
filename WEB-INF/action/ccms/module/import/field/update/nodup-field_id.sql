select field_id from t_import_field
where field_id = ${fld:field_id}
and tab_id = ${fld:tab_id}
and tuid != ${fld:tuid}
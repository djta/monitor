select tuid from t_import_rule_filter
where parent_id = ${fld:id}
limit 1

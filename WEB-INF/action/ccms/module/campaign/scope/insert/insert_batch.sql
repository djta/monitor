  insert into cs_campaign_scope
  (tuid
  ,campaign_id
  ,scope_type
  ,scope_value
  ,start_date
  ,end_date
  ,created
  ,createdby
  )
  values(
  ${seq:nextval@${schema}seq_default}
  ,${fld:campaign_id}
  ,${scope_type}
  ,${fld:${scope_value}}
  ,${fld:start_date}
  ,${fld:end_date}
  ,{ts '${def:timestamp}'}
  ,'${def:user}'
 );
 

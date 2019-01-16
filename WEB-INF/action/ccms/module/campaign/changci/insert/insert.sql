  insert into cs_campaign_changci
  (tuid
  ,campaign_id
  ,changci_name
  ,changci_time
  ,regist_from_time
  ,regist_to_time
  ,event_city
  ,car_series
  ,show_order
  ,changci_quota
  ,remark
  ,created
  ,createdby
  ,updated
  ,updatedby)
  values(
  ${seq:nextval@seq_campaign_changci}
  ,${fld:campaign_id}
  ,${fld:changci_name}
  ,${fld:changci_time}
  ,{ts ${fld:regist_from_time}}
  ,{ts ${fld:regist_to_time}}
  ,${fld:event_city}
  ,${fld:car_series}
  ,${fld:show_order}
  ,${fld:changci_quota}
  ,${fld:remark}
  ,{ts '${def:timestamp}'}
	,'${def:user}'
	,{ts '${def:timestamp}'}
	,'${def:user}'
  );
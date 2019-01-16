UPDATE
	cs_campaign_changci
SET
	changci_name=${fld:changci_name}
	,changci_time=${fld:changci_time}
	,regist_from_time={ts ${fld:regist_from_time}}
	,regist_to_time={ts ${fld:regist_to_time}}
	,event_city=${fld:event_city}
	,car_series=${fld:car_series}
	,show_order=${fld:show_order}
	,changci_quota=${fld:changci_quota}
	,remark=${fld:remark}
	,updated={ts '${def:timestamp}'}
	,updatedby='${def:user}'
WHERE
	tuid=${fld:tuid}
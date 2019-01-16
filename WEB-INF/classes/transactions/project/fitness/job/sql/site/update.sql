UPDATE cc_siteusedetail 
SET status=0,
	remark=concat(remark, '，系统提示：该预约已超过系统保留支付时间，自动取消！')
where created+ INTERVAL ${fld:sitedate_value} <= '${def:timestamp}'::timestamp 
and paystatus=0 and status!=0
and org_id=${fld:org_id}

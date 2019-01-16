select 
	code,
	sitename,
	block_price,--包场价格（元/小时）
	block_maxnum,--包场最大人数
	group_price,--拼场价格（元/小时）
	group_minnum,--拼场最小人数
	group_maxnum--拼场最大人数
from cc_sitedef
where status = 1 and org_id = ${def:org}
and (case when ${fld:sitetype} is null then 1=1 else sitetype = ${fld:sitetype} end)
order by created desc
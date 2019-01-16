select (case when (select 
	choose_way
from cc_site_timelimit
where sitecode=${fld:sitecode} and limittime=${fld:limittime}
and org_id= ${def:org} limit 1) is null then null else (select 
	choose_way
from cc_site_timelimit
where sitecode=${fld:sitecode} and limittime=${fld:limittime}
and org_id= ${def:org} limit 1) end) as choose_way,
(case when (select 
	code
from cc_site_timelimit
where sitecode=${fld:sitecode} and limittime=${fld:limittime}
and org_id= ${def:org} limit 1) is null then null else (select 
	code
from cc_site_timelimit
where sitecode=${fld:sitecode} and limittime=${fld:limittime}
and org_id= ${def:org} limit 1) end) as site_timelimitcode,
(select 
	count(1) as sdnum
from cc_siteusedetail
where sitecode=${fld:sitecode} and status>0 and org_id = ${def:org}
and prepare_date = ${fld:prepare_date}::date 
and ${fld:times}::time>=prepare_starttime and ${fld:times}::time<prepare_endtime) as sdnum
from dual;




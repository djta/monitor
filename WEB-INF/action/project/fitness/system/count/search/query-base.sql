select
*
from
(
select  139 as col1,'一号线' as col2,'东单站' as col3,'售票口1' as col4,0 as col5,0 as col6,0 as col7,'0分0秒' as col8,'0分0秒' as col9
union all 
select  140 as col1,'一号线' as col2,'东单站' as col3,'售票口2' as col4,0 as col5,0 as col6,0 as col7,'0分0秒' as col8,'0分0秒' as col9

) m
 where 1=1
${filter}


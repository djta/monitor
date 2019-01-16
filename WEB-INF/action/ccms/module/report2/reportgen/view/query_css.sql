select
	'BACKGROUND-COLOR: #f5f5f5; font:bold 12px/24px Arial, Helvetica, sans-serif;text-align:center;' as report_grouptotal --MyReport_groupTotal,MyReport_groupPercent
	,'BACKGROUND-COLOR: #f5f5f5; font:bold 12px/24px Arial, Helvetica, sans-serif;text-align:center;' as report_head --MyReport_head
	,'BACKGROUND-COLOR: #f5f5f5; font:bold 12px/24px Arial, Helvetica, sans-serif;text-align:center;' as report_total --MyReport_total,MyReport_totalPercent
	,'font:bold 12px/24px Arial, Helvetica, sans-serif;text-align:right;' as report_title --MyReport_title
	,'font:normal 12px/24px Arial, Helvetica, sans-serif;text-align:center;' as report_data --MyReport_data , 平面报表、分组报表
	,'font:normal 12px/24px Arial, Helvetica, sans-serif;text-align:center;' as cross_report_data --MyReport_data ,交叉报表
	,'BACKGROUND-COLOR: #f5f5f5; font:bold 12px/24px Arial, Helvetica, sans-serif;text-align:left;' as report_crossheadhead --MyReport_cross_head_head
from
	dual
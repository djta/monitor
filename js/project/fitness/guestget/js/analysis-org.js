
/*** 未续费会籍客户 */
function guestnummonthFun(){
	this.legendData = [''];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		var d = new Date($("#monthdateinfo").val());
		//当前日期加一月
		d.setMonth(d.getMonth() +1);
		//减一天
		d.setDate(d.getDate()-1);
		this.loadCharts($("#monthdateinfo").val(), d.format("yyyy-MM-dd"), 0);
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/guestget/persontask/guestnummonth?fdate="+fdate+"&tdate="+tdate+"&datatype=bar";
		getAjaxCall(uri, false, function(){
		});
	}
}



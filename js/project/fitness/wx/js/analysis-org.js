
function cardsales(url, name){
	this.legendData = [name];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		this.loadCharts();
	},
	/** 加载数据 */
	this.loadCharts = function(){
		getAjaxCall(url, false, function(){
		});
	}
}



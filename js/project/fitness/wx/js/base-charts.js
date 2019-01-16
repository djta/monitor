require.config({
	paths: {
		echarts: contextPath + '/js/echarts-2.2.7/build/dist'
	}
});
/** 切换视图模式 */
function changeMagicType(obj, id, magicType){
	if(obj==undefined||obj.total==0){
		//无数据显示图片
		$("#"+id).html("<img src='"+contextPath+"/js/project/fitness/image/SVG/echarts/no_data.png' />");
	}else{
		$("#"+id).empty();
		var mtype = (typeof(magicType) != "undefined" && "bar" == magicType ? magicType : "line");
		if( "list" == magicType ){
			showDatagrid(id, obj, '');
		}else if( "pie" == magicType ){
			initPieCharts(id, obj, magicType);
		}else{
			initLineBarCharts(id, obj, mtype);
		}
	}
}
/** 切换视图模式（百分比） */
function changeMagicTypePercentage(obj, id, magicType){
	if(obj==undefined||obj.total==0){
		//无数据显示图片
		$("#"+id).html("<img src='"+contextPath+"/js/project/fitness/image/SVG/echarts/no_data.png' />");
	}else{
		$("#"+id).empty();
		var mtype = (typeof(magicType) != "undefined" && "bar" == magicType ? magicType : "line");
		if( "list" == magicType ){
			showDatagrid(id, obj, '1');
		}else if( "pie" == magicType ){
			initPieCharts(id, obj, magicType);
		}else{
			initLineBarChartsPercentage(id, obj, mtype);
		}
	}
}
/*** 切换到表格视图 */
function showDatagrid(id, obj, percentage){
	if(percentage=="1"){
		percentage="%";
	}
	var gridstr = '<div class="to-change-background" style="height:100%;overflow:auto;overflow-x:hidden;">';
	gridstr += '<table class="am-table"><thead><tr><th>时间</th>';
	var colsnum = obj.legendData.length;	// 列数
	// 拼接列头
	for( var i = 0; i < colsnum; i++ ){
		gridstr += '<th>'+obj.legendData[i]+'</th>';
	}
	gridstr += '</tr></thead><tbody>';
	// 拼接数据
	for( var i = 0; i < obj.total; i++ ){
		var colstr = "", colstotal = 0;
		for( var j = 0; j < colsnum; j++ ){
			colstotal += obj.seriesDataArr[j][i];
			colstr += '<td data-series="'+obj.legendData[j]+'">' + obj.seriesDataArr[j][i] + percentage +'</td>';
		}
		if( colstotal > 0 ){	/** 只显示统计数据不全为0的行 */
			gridstr += '<tr>';
			gridstr += '<td>' + obj.xdataArr[i] + '</td>';
			gridstr += colstr;
			gridstr += '</tr>';
		}
	}
	gridstr += '</tbody></table></div>';
	$("#"+id).append(gridstr);
}

/** 加载柱状/曲线图表 */
function initLineBarCharts(id, obj, magicType){
	$("#"+id).empty();
	var mtype = (typeof(magicType) != "undefined" && "bar" == magicType ? magicType : "line");
	var seriesData = [];
	for( var i = 0; i < obj.seriesDataArr.length; i++ ){
		seriesData[i] = {
			name: obj.legendData[i],
			type: mtype,
			data: obj.seriesDataArr[i]
		};
	}
	
	require(
		[
			'echarts',
			'echarts/chart/line',
			'echarts/chart/bar'
		],
		function (ec) {
			obj.myChart = ec.init(document.getElementById(id));
			var option = {
				grid: {
					show: true,
					borderWidth: 0
				},
				tooltip: {
					trigger: 'axis',
					axisPointer: {
						type: 'cross',
						crossStyle: {
							 color: '#999'
						}
					}
				},
				legend: {
					show: obj.isLegendShow,
					data: obj.legendData,
					textStyle: {
						color: 'black'
					}
				},
				xAxis: [
					{
						type: 'category',
		                splitLine: {show: false},	/** 去除网格线 */
		                splitArea: {show: false},	/** 不保留网格区域 */
						data: obj.xdataArr,
						axisPointer: {
							type: 'shadow'
						},
						axisLabel: {
							textStyle: {
								color: 'black'
							}
						}
					}
				],
				yAxis: [
					{
						type: 'value',
		                splitLine: {show: false},	/** 去除网格线 */
		                splitArea: {show: false},	/** 不保留网格区域 */
						name: '',
						axisLabel: {
							formatter: '{value}',
							textStyle: {
								color: 'black'
							}
						}
					}
				],
				series: seriesData
			};
			obj.myChart.setOption(option);
			window.onresize = obj.myChart.resize;
			
			
			/**if( "posDiv" == id ){

				var newposCharts = obj;
				newposCharts.total = 3;

				newposCharts.legendData = ['newPOS','newP1','newP2'];
				newposCharts.xdataArr = ["2018-02-10","2018-02-15","2018-02-25"];
				newposCharts.seriesDataArr = [[50,30,70], [30,10,20], [20,20,50]];
				addLineBar(obj, newposCharts, magicType);
			}*/

		}
	);
	return obj;
}

function addLineBar(obj, newobj, magicType){
	var mtype = (typeof(magicType) != "undefined" && "bar" == magicType ? magicType : "line");
	console.log(obj.myChart);
	var option = obj.myChart.getOption();
	var seriesData = [];
	for( var i = 0; i < newobj.seriesDataArr.length; i++ ){
		seriesData[i] = {
			name: newobj.legendData[i],
			type: mtype,
			data: newobj.seriesDataArr[i]
		};
		
		option.legend.data.push(newobj.legendData[i]);
		option.series.push(seriesData[i]);
	}
	obj.myChart.setOption(option);
	window.onresize = obj.myChart.resize;
}

/** 加载柱状/曲线图表（百分比） */
function initLineBarChartsPercentage(id, obj, magicType){
	$("#"+id).empty();
	var mtype = (typeof(magicType) != "undefined" && "bar" == magicType ? magicType : "line");
	var seriesData = [];
	for( var i = 0; i < obj.seriesDataArr.length; i++ ){
		seriesData[i] = {
			name: obj.legendData[i],
			type: mtype,
			data: obj.seriesDataArr[i]
		};
	}
	
	require(
		[
			'echarts',
			'echarts/chart/line',
			'echarts/chart/bar'
		],
		function (ec) {
			obj.myChart = ec.init(document.getElementById(id));
			var option = {
				grid: {
					show: true,
					borderWidth: 0
				},
				tooltip: {
					trigger: 'item',
					formatter: "{a} <br/>{b} : {c}%"
				},
				legend: {
					show: obj.isLegendShow,
					data: obj.legendData,
					textStyle: {
						color: 'black'
					}
				},
				xAxis: [
					{
						type: 'category',
		                splitLine: {show: false},	/** 去除网格线 */
		                splitArea: {show: false},	/** 不保留网格区域 */
						data: obj.xdataArr,
						axisPointer: {
							type: 'shadow'
						},
						axisLabel: {
							textStyle: {
								color: 'black'
							}
						}
					}
				],
				yAxis: [
					{
						type: 'value',
		                splitLine: {show: false},	/** 去除网格线 */
		                splitArea: {show: false},	/** 不保留网格区域 */
						name: '',
						axisLabel: {
							formatter: '{value}',
							textStyle: {
								color: 'black'
							}
						}
					}
				],
				series: seriesData
			};
			obj.myChart.setOption(option);
			window.onresize = obj.myChart.resize;
		}
	);
	return obj;
}


/** 加载饼图图表 */
function initPieCharts(id, obj, magicType){
	var seriesData = [];
	for( var i = 0; i < obj.seriesDataArr.length; i++ ){
		seriesData[i] = {
			name: obj.legendData[i],
			value: obj.seriesDataArr[i]
		};
	}
	
	require(
		[
			'echarts',
			'echarts/chart/pie'
		],
		function (ec) {
			obj.myChart = ec.init(document.getElementById(id));
			var option = {
				tooltip: {
					trigger: 'item',
					formatter: "{a} <br/>{b}: {c} ({d}%)"
				},
				legend: {
					orient: 'vertical',
					x: 'left',
					data: obj.legendData,
					textStyle: {
						color: 'black'
					}
				},
				series: [
					{
						name: obj.titlename,
						type:'pie',
						radius: ['40%', '60%'],
						avoidLabelOverlap: false,
						label: {
							normal: {
								show: false,
								position: 'center'
							},
							emphasis: {
			                     show: true,
			                     textStyle: {
			                         fontSize: '30',
			                         fontWeight: 'bold'
			                     }
			                 }
			             },
			             labelLine: {
			                 normal: {
			                     show: false
			                 }
			             },
			             data: seriesData
			         }
				]
			};
			obj.myChart.setOption(option);
			window.onresize = obj.myChart.resize;
		}
	);
	return obj;
}


/*** 视图模式切换绑定 */
function bindChangeMagicType(obj, dataArea){
	// 视图模式
	$("button[name=changeMagicType][data-area="+dataArea+"].active").removeClass("active");
	$("button[name=changeMagicType][data-area="+dataArea+"][data-type=line]").addClass("active");
	$("button[name=changeMagicType][data-area="+dataArea+"]").unbind().on("click",function(){
		$(this).siblings(".active").removeClass("active");
		$(this).addClass("active");
		changeMagicType(obj, dataArea+"Div", $(this).data("type"));
	});
	// 按天、按周、按月
	$("select[data-name=datatype][data-area="+dataArea+"]").unbind().on("change",function(){
		obj.loadCharts();
	});
}

/*** 创建charts对象 */
function chartsClass(){
	var obthis = this;
	obthis.obj;
	obthis.idx = 0;
	obthis.total = 0;
	obthis.isLegendShow = false;
	obthis.legendData = [], obthis.xdataArr = [], seriesDataArr = [];
	obthis.titlename = "";
	obthis.baseuri = "";
	obthis.dataArea = "";
	/** 初始化 */
	obthis.init = function(option){
		if( undefined != option.titlename && null != option.titlename ){
			obthis.titlename = option.titlename;
		}
		if( undefined != option.dataArea && null != option.dataArea ){
			obthis.dataArea = option.dataArea;
		}
		if( undefined != option.idx && null != option.idx ){
			obthis.idx = option.idx;
		}
		if( undefined != option.isLegendShow && null != option.isLegendShow ){
			obthis.isLegendShow = option.isLegendShow;
		}
		if( undefined != option.legendData && null != option.legendData ){
			obthis.legendData = option.legendData;
		}
		obthis.baseuri = option.uri;
	},
	/** 加载charts */
	obthis.loadCharts = function(){
		var fdate = $("#"+obthis.dataArea+"fdate").val();
		var tdate = $("#"+obthis.dataArea+"tdate").val();
		var datatype = $("#"+obthis.dataArea+"datatype").val();
		$("#"+obthis.dataArea+"daterangespick").html(fdate + " ~ " + tdate);
		var compareflag = $("li[data-name=compareflag][data-area="+obthis.dataArea+"].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = obthis.baseuri + "?idx="+obthis.idx+"&fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, true, function(){
			$("#"+obthis.dataArea+"daterangespan").html(fdate + " ~ " + tdate);
			bindChangeMagicType(obthis.obj, obthis.dataArea);
		});
	}
}

/** 创建曲线图对象 */
function lineCharts(id){
	var obthis = this;
	obthis.id = id;
	obthis.chartsobj = null;
	obthis.isLegendShow = false;
	obthis.boundaryGap = true;
	obthis.createCharts = function(legendData, xdata, seriesData){
		require(
			[
				'echarts',
				'echarts/chart/line'
			],
			function (ec) {
				obthis.chartsobj = ec.init(document.getElementById(obthis.id));
				obthis.updateOption(legendData, xdata, seriesData);
			}
		);
	},
	obthis.setConfig = function(map){
		obthis.isLegendShow = undefined != map.isLegendShow && null != map.isLegendShow && true == map.isLegendShow ? true : false;
		obthis.boundaryGap = undefined != map.boundaryGap && null != map.boundaryGap && false == map.boundaryGap ? false : true;
	},
	obthis.updateOption = function(legendData, xdata, seriesData){
		if( null == obthis.chartsobj ){
			return false;
		}
		var option = {
			grid: {
				show: true,
				borderWidth: 0,
		        containLabel: false
			},
			tooltip: {
				trigger: 'axis',
				axisPointer: {
					type: 'cross',
					crossStyle: {
						 color: '#999'
					}
				}
			},
			legend: {
				show: obthis.isLegendShow,
				data: legendData,
				textStyle: {
					color: 'black'
				}
			},
			xAxis: [
				{
					type: 'category',
	                splitLine: {show: false},	/** 去除网格线 */
	                splitArea: {show: false},	/** 不保留网格区域 */
	                boundaryGap : obthis.boundaryGap,
					data: xdata,
					axisPointer: {
						type: 'shadow'
					},
					axisLabel: {
						textStyle: {
							color: 'black'
						}
					}
				}
			],
			yAxis: [
				{
					type: 'value',
	                splitLine: {show: false},	/** 去除网格线 */
	                splitArea: {show: false},	/** 不保留网格区域 */
					name: '',
					axisLabel: {
						formatter: '{value}',
						textStyle: {
							color: 'black'
						}
					}
				}
			],
			series: seriesData
		};
		obthis.chartsobj.setOption(option);
		window.onresize = obthis.chartsobj.resize;
	}
}


/** 创建漏斗图对象 */
function funnelCharts(id){
	var obthis = this;
	obthis.chartsobj = null;
	obthis.id = id;
	obthis.title = "漏斗图";
	obthis.subtitle = "";
	obthis.legendData = [];
	obthis.isTitleShow = true;
	obthis.isLegendShow = false;
	obthis.createCharts = function(seriesData){
		require(
			[
				'echarts',
				'echarts/chart/funnel'
			],
			function (ec) {
				obthis.chartsobj = ec.init(document.getElementById(obthis.id));
				if( undefined != seriesData && seriesData.length > 0 ){
					obthis.updateOption(seriesData);
				}
			}
		);
	},
	obthis.setConfig = function(map){
		obthis.title = undefined != map.title && null != map.title && "" != map.title ? map.title : obthis.title;
		obthis.subtitle = undefined != map.subtitle && null != map.subtitle && "" != map.subtitle ? map.subtitle : obthis.subtitle;
		obthis.isTitleShow = undefined != map.isTitleShow && null != map.isTitleShow && false == map.isTitleShow ? false : true;
		obthis.isLegendShow = undefined != map.isLegendShow && null != map.isLegendShow && true == map.isLegendShow ? true : false;
	},
	obthis.updateOption = function(seriesData){
		var dataArr = [];
		for(var i = 0; i < seriesData.length; i++){
			var seriesJson = seriesData[i];
			if( seriesJson["value"] > 0 ){
				dataArr[dataArr.length] = {value: seriesJson["value"], name: seriesJson["name"]};
			}
		}
		dataArr = dataArr.sort(function (a, b) {return a.value - b.value;});
		for(var i = 0; i < dataArr.length; i++){
			obthis.legendData[i] = dataArr[i]["name"];
		}
		
		var option = {
			title: {
				show: obthis.isTitleShow,
				text: obthis.title,
				subtext: obthis.subtitle
			},
			tooltip: {
				trigger: 'item',
				formatter: "{a} <br/>{b} : {c}%"
			},
			legend: {
				show: obthis.isLegendShow,
				data: obthis.legendData,
				textStyle: {
					color: 'black'
				}
			},
			series: [
				{
					name: obthis.title,
					type: 'funnel',
					x: '5%',
					width: '90%',
					y: obthis.isLegendShow ? '20%' : '5%',
					height: obthis.isLegendShow ? "65%" : "80%",
					minSize: '0%',
					maxSize: '100%',
					sort: 'descending',
					gap: 1,
					label: {
						normal: {
							formatter: function (params) {
								return params.name + ' ' + params.value + '%';
							},
							position: 'inside'
						},
						emphasis: {
							textStyle: {
								fontSize: 20
							}
						}
					},
					labelLine: {
						normal: {
							length: 10,
							lineStyle: {
								width: 1,
								type: 'solid'
							}
						}
					},
					itemStyle: {
						normal: {
							borderWidth: 0,
							shadowBlur: 30,
							shadowOffsetX: 0,
							shadowOffsetY: 10,
							shadowColor: 'rgba(0, 0, 0, 0.5)'
						}
					},
					data: dataArr
				}
			]
		};
		obthis.chartsobj.setOption(option);
		window.onresize = obthis.chartsobj.resize;
	}
}

function pieMultipleCharts(id){
	var obthis = this;
	obthis.id = id;
	obthis.chartsobj = null;
	obthis.legendData = [];
	obthis.isLegendShow = false;
	obthis.createCharts = function(seriesArr){
		require(
			[
				'echarts',
				'echarts/chart/pie'
			],
			function (ec) {
				obthis.chartsobj = ec.init(document.getElementById(obthis.id));
				if( undefined != seriesArr && seriesArr.length > 0 ){
					obthis.updateOption(seriesArr);
				}
			}
		);
	},
	obthis.setConfig = function(map){
		obthis.isLegendShow = undefined != map.isLegendShow && null != map.isLegendShow && true == map.isLegendShow ? true : false;
		obthis.legendData = undefined != map.legendData && null != map.legendData && map.legendData.length > 0 ? map.legendData : obthis.legendData;
	},
	obthis.updateOption = function(seriesArr){
		var option = {
			tooltip: {
				trigger: 'item',
				formatter: "{a} <br/>{b}: {c} ({d}%)"
			},
			legend: {
				show: obthis.isLegendShow,
				orient: 'vertical',
				x: 'left',
				data: obthis.legendData,
				textStyle: {
					color: 'black'
				}
			},
			series: seriesArr
		};
		obthis.chartsobj.setOption(option);
		window.onresize = obthis.chartsobj.resize;
	}
}

function pieCharts(id){
	var obthis = this;
	obthis.id = id;
	obthis.chartsobj = null;
	obthis.legendData = [];
	obthis.isLegendShow = false;
	obthis.titlename = "";
	obthis.setConfig = function(map){
		obthis.isLegendShow = undefined != map.isLegendShow && null != map.isLegendShow && true == map.isLegendShow ? true : false;
		obthis.legendData = undefined != map.legendData && null != map.legendData && map.legendData.length > 0 ? map.legendData : obthis.legendData;
		obthis.titlename = undefined != map.titlename && null != map.titlename ? map.titlename : obthis.titlename;
	},
	obthis.createCharts = function(seriesData){
		require(
			[
				'echarts',
				'echarts/chart/pie'
			],
			function (ec) {
				obthis.chartsobj = ec.init(document.getElementById(obthis.id));
				if( undefined != seriesData && seriesData.length > 0 ){
					obthis.updateOption(seriesData);
				}
			}
		);
	},
	obthis.updateOption = function(seriesData){
		obthis.legendData = [];
		for( var i = 0; i < seriesData.length; i++ ){
			obthis.legendData[i] = seriesData[i]["name"];
		}
		
		var option = {
			tooltip: {
				trigger: 'item',
				formatter: "{a} <br/>{b}: {c} ({d}%)"
			},
			legend: {
				orient: 'vertical',
				x: 'left',
				data: obthis.legendData,
				textStyle: {
					color: 'black'
				}
			},
			series: [
				{
					name: obthis.titlename,
					type:'pie',
					radius: [0, '60%'],
					avoidLabelOverlap: false,
					label: {
						normal: {
							show: false,
							position: 'center'
						},
						emphasis: {
		                     show: true,
		                     textStyle: {
		                         fontSize: '30',
		                         fontWeight: 'bold'
		                     }
		                 }
		             },
		             labelLine: {
		                 normal: {
		                     show: false
		                 }
		             },
		             data: seriesData
		         }
			]
		};
		obthis.chartsobj.setOption(option);
		window.onresize = obthis.chartsobj.resize;
	}
}

//饼图列表
function pielist(id, seriesArr, name){
	var custhtml = '<div class="to-change-background" style="height:100%;overflow:auto;overflow-x:hidden;"><table class="am-table">';
	custhtml += '<tr><td>类型</td><td>数值</td></tr>';
	for(var i = 0;i<seriesArr.length;i++){
		custhtml += '<tr><td>'+seriesArr[i].name+'</td><td>'+seriesArr[i].value+'</td></tr>';
	}
	$("#"+id+"Divlist").html(custhtml+"</table></div>");
}

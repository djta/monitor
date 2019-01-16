/** 
 * 水球图工具类，依赖于：
 *  /js/echarts-2.2.7/liquidfill/echarts.js
 *  /js/echarts-2.2.7/liquidfill/echarts-liquidfill.min.js
 */
function liquidFillCharts(id){
	var obthis = this;
	obthis.chartsobj = null;
	obthis.seriesName = "";
	// 初始化
	obthis.chartsobj = echarts.init(document.getElementById(id));

	obthis.setConfig = function(map){
		obthis.seriesName = undefined != map.seriesName && null != map.seriesName ? map.seriesName : "";
	},
	// 设置加载数据
	obthis.updateOption = function(data, num){
		var option = {
			series: [{
				type: 'liquidFill',
				radius: '80%',
				data: data,
				name: obthis.seriesName,
				amplitude:0,
				waveAnimation: false,	/** 动态波浪有内存泄漏bug */
				outline: {
					show: false
				},
				backgroundStyle: {
					borderWidth: 1,
					borderColor: 'rgba(51,143,226,1)',	/** 容器边框颜色 */
					/**color: bgColor,*/	/** 容器背景色 */
					shadowColor: 'rgba(51,143,226,1)',	/** 容器边框阴影部分渐变色 */
					shadowBlur: 20
				},
				label: {
					normal: {
						/**formatter: num+"",
						formatter: function(param) {
							return param.seriesName + '\n' + param.value;
						},*/
						formatter: function(param) {
							if( "" == param.seriesName ){
								return num + "";
							}else{
								return param.seriesName + '\n' + num;
							}
						},
						textStyle: {
							color: 'rgba(51,143,226,1)',
							insideColor: 'rgba(51,143,226,1)',
							fontSize: 35
						}
					}
				}
			}]
		};
		obthis.chartsobj.setOption(option);
		window.onresize = obthis.chartsobj.resize;
	}
}

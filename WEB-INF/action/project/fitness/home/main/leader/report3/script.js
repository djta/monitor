var dom = document.getElementById("mcsaleDiv4");
var myChart = echarts.init(dom);
var app = {};
option = null;
var dataStyle = {
	    normal: {
	        label: {show:false},
	        labelLine: {show:false}
	    }
	};
	var placeHolderStyle = {
	    normal : {
	        color: 'rgba(0,0,0,0)',
	        label: {show:false},
	        labelLine: {show:false}
	    },
	    emphasis : {
	        color: 'rgba(0,0,0,0)'
	    }
	};
	option = {
	    title: {
	        text: '服务器资源监控',
	        x: 'center',
	        y: 'center',
	        itemGap: 20,
	        textStyle : {
	            color : 'rgba(30,144,255,0.8)',
	            fontFamily : '微软雅黑',
	            fontSize : 35,
	            fontWeight : 'bolder'
	        }
	    },
	    tooltip : {
	        show: true,
	        formatter: "{a} <br/>{b} : {c} ({d}%)"
	    },
	    legend: {
	        x : document.getElementById('mcsaleDiv4').offsetWidth / 2,
	        y : 45,
	        itemGap:12,
	        data:['内存','硬盘','cpu'],
	        backgroundColor: '#237d4f'
	    },
	    toolbox: {
	        show : true,
	        feature : {
	            mark : {show: true},
	            dataView : {show: true, readOnly: false},
	            restore : {show: true},
	            saveAsImage : {show: true}
	        }
	    },
	    series : [
	        {
	            name:'内存',
	            type:'pie',
	            clockWise:false,
	            radius : [125, 150],
	            itemStyle : dataStyle,
	            data:[
	                {
	                    value:80,
	                    name:'已使用'
	                },
	                {
	                    value:20,
	                    name:'剩余',
	                    itemStyle : placeHolderStyle
	                }
	            ]
	        },
	        {
	            name:'硬盘',
	            type:'pie',
	            clockWise:false,
	            radius : [100, 125],
	            itemStyle : dataStyle,
	            data:[
	                {
	                    value:40, 
	                    name:'已使用'
	                },
	                {
	                    value:60,
	                    name:'剩余',
	                    itemStyle : placeHolderStyle
	                }
	            ]
	        },
	        {
	            name:'cpu',
	            type:'pie',
	            clockWise:false,
	            radius : [75, 100],
	            itemStyle : dataStyle,
	            data:[
	                {
	                    value:58, 
	                    name:'占比'
	                },
	                {
	                    value:42,
	                    name:'已使用',
	                    itemStyle : placeHolderStyle
	                }
	            ]
	        }
	    ]
	};
	                    
	                    
	                    
if (option && typeof option === "object") {
    myChart.setOption(option, true);
}
var dom = document.getElementById("mcsaleDiv3");
var myChart = echarts.init(dom);
var app = {};
option = null;
option = {
	    title : {
	        text: '预警监控',
	        backgroundColor: '#dd514c'
	    },
	    tooltip : {
	        trigger: 'axis'
	    },
	    legend: {
	    	backgroundColor: '#237d4f',
	        data:['高','中','低']
	    },
	    toolbox: {
	        show : true,
	        feature : {
	            mark : {show: true},
	            dataView : {show: true, readOnly: false},
	            magicType : {show: true, type: ['line', 'bar']},
	            restore : {show: true},
	            saveAsImage : {show: true}
	        }
	    },
	    calculable : true,
	    xAxis : [
	        {
	            type : 'category',
	            data : ['8点','10点','12点','14点','16点','18点','20点','22点'],
	            axisLabel : {
                formatter: '{value}',
                textStyle: {
                    color: '#00000'
                }
            }
	        }
	    ],
	    yAxis : [
	        {
	            type : 'value',
	            axisLabel : {
                    formatter: '{value}',
                    textStyle: {
                        color: '#00000'
                    }
                }
	        }
	    ],
	    series : [
	        {
	            name:'高',
	            type:'line',
	            data:[2, 6, 0, 2, 3, 1, 0, 1]
	        },
	        {
	            name:'中',
	            type:'bar',
	            data:[1, 2, 1, 2, 1, 1, 0, 3]
	        },
	        {
	            name:'低',
	            type:'bar',
	            data:[4, 1, 0, 2, 1, 0, 0, 1]
	        }
	    ]
	};
	                    
	                    
if (option && typeof option === "object") {
    myChart.setOption(option, true);
}
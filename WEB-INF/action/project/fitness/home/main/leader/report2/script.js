var dom = document.getElementById("mcsaleDiv2");
var myChart = echarts.init(dom);
var app = {};
option = null;
option = {
	    title: {
	        text: '设备运行监控',
	        backgroundColor: '#04c'
	    },
	    tooltip : {
	        trigger: 'item',
	        formatter: "{a} <br/>{b} : {c} ({d}%)"
	    },
	    legend: {
	    	backgroundColor: '#237d4f',	        data:['正常运行','异常报警']
	    },
	    toolbox: {
	        show : true,
	        feature : {
	            mark : {show: true},
	            dataView : {show: true, readOnly: false},
	            magicType : {
	                show: true, 
	                type: ['pie', 'funnel'],
	                option: {
	                    funnel: {
	                        x: '25%',
	                        width: '50%',
	                        funnelAlign: 'center',
	                        max: 1548
	                    }
	                }
	            },
	            restore : {show: true},
	            saveAsImage : {show: true}
	        }
	    },
	    calculable : true,
	    series : [
	        {
	            name:'',
	            type:'pie',
	            itemStyle : {
	                normal : {
	                    label : {
	                        show : true
	                    },
	                    labelLine : {
	                        show : true
	                    }
	                    ,color: function(params) {
                            // build a color map as your need.
                            var colorList = [
                              '#5eb95e','#dd514c'
                            ];
                            return colorList[params.dataIndex]
                        }
	                },
	                emphasis : {
	                    label : {
	                        show : true,
	                        position : 'center',
	                        textStyle : {
	                            fontSize : '30',
	                            fontWeight : 'bold'
	                        }
	                    }
	                }
	            },
	            data:[
	                {value:100, name:'正常运行'},
	                {value:2, name:'异常报警'}
	            ]
	        }
	    ]
	};
	                    
	                    
if (option && typeof option === "object") {
    myChart.setOption(option, true);
}
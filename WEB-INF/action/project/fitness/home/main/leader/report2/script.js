var dom = document.getElementById("mcsaleDiv");
var myChart = echarts.init(dom);
var app = {};
option = null;
option = {
    title: {
        text: '人流量统计',
        backgroundColor: '#d23232'
    },
    tooltip : {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            label: {
                backgroundColor: '#00000'
            }
        }
    },
    legend: {
    	backgroundColor: '#237d4f',
        data:['出站','入站']
    },
    toolbox: {
        feature: {
            saveAsImage: {}
        }
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis : [
        {
            type : 'category',
            boundaryGap : false,
            data : ['0点','6点','12点','18点','24点'],
            axisLabel: {
                show: true,
                textStyle: {
                    color: '#7d2340'
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
                        color: '#7d6624'
                    }
                }
        }
    ],
    series : [
        {
            name:'出站',
            type:'line',
            stack: '总量',
            areaStyle: {},
            data:[1200, 1320, 1010, 1340, 900]
        },
        {
            name:'入站',
            type:'line',
            stack: '总量',
            label: {
                normal: {
                    show: true,
                    position: 'top'
                }
            },
            areaStyle: {normal: {}},
            data:[820, 932, 901, 934, 1290]
        }
    ]
};
;
if (option && typeof option === "object") {
    myChart.setOption(option, true);
}
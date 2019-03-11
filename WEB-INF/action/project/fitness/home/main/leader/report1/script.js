var dom = document.getElementById("mcsaleDiv1");
var myChart = echarts.init(dom);
var app = {};
option = null;
option = {
    title: {
        text: '人流数据监测',
        backgroundColor: '#f37b1d'
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
        data:['东单','和平里北街']
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
            data : ['00:00:00','02:00:00','04:00:00','06:00:00','08:00:00','12:00:00','14:00:00','16:00:00','18:00:00','22:00:00'],
            axisLabel: {
                show: true,
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
            name:'东单',
            type:'line',
            stack: '总量',
            areaStyle: {},
            data:[1200, 1320, 1010, 1340, 900,1200, 1320, 1010, 1340, 100]
        },
        {
            name:'和平里北街',
            type:'line',
            stack: '总量',
            label: {
                normal: {
                    show: true,
                    position: 'top'
                }
            },
            areaStyle: {normal: {}},
            data:[820, 932, 901, 934, 1290,820, 932, 901, 934, 50]
        }
    ]
};
;
if (option && typeof option === "object") {
    myChart.setOption(option, true);
}
function $HighChart(id,map){//id 为div id
	return new $HighChartShow(id,map);
}

function $HighChartShow(id,map) {
	this.chartid=id;
	this.title='';
	this.isShow3D=false;
	this.chartType='column';//图表类型
	this.xAxisType='linear';//默认
	this.execBeforeScript='';//显示图表前脚本
	if(map==undefined){
		map={};
	}
	if(map['success']!=undefined){
		this.success=map['success'];
	}
	if(map['config']!=undefined){
		this.callBackConfig=map['config'];
	}
	this.searchChart = function() {
		var chartObj={'title':'你们好','titleY':'数量',chart_type:4,field_x:'name',field_y:'total',field_z:'type',isShow3D:true};
	
		var dataArray=[];
		dataArray.push({name:'苹果',type:'北京',total:1});
		dataArray.push({name:'香蕉',type:'北京',total:2});
		dataArray.push({name:'苹果',type:'上海',total:4});
		dataArray.push({name:'香蕉',type:'上海',total:4});
		var date=new Date();
		var time=date.getTime();
		var time2=time+24*60*60*1000;
		/*dataArray.push({name:time,type:'北京',total:1});
		dataArray.push({name:time,type:'北京',total:2});
		dataArray.push({name:time2,type:'上海',total:3});
		dataArray.push({name:time2,type:'上海',total:4});*/
		
		dataArray.push({});
		this.getChartResult(chartObj, dataArray);//chartObj 为图表配置对象 dataArray为数据结果数组
	},this.getChartResult = function(chartObj,data) {//接口   
		this.title=chartObj.title;
		this.titleY=chartObj.titleY;//linear，logarithmic，或者datetime。
		if(chartObj.datetime!=undefined){
			this.xAxisType=chartObj.datetime;
		}
		if(chartObj.isShow3D!=undefined){//是否3D
			this.isShow3D=chartObj.isShow3D;
		}
		if(chartObj.beforeScript!=undefined){//显示图表前脚本
			this.execBeforeScript=chartObj.beforeScript;
		}
		var chartType=chartObj.chart_type;
		if(chartType!=undefined){//line, spline, area, areaspline,column, bar, pie , scatter
			if(chartType==0){
				this.chartType='column';//柱状图
			}else if(chartType==1){
				this.chartType='spline';//折线图
			}else if(chartType==2){
				this.chartType='pie';//饼图
			}else if(chartType==3){
				this.chartType='area';//区域图
			}else if(chartType==4){
				this.chartType='combination';//复合图
			}
		}
		var field_x=chartObj.field_x;//改这里
		var field_y=chartObj.field_y;
		var field_z=chartObj.field_z;
		if(field_z==undefined){
			field_z=' ';
		}
		var chartArray=[];
		//data.pop();
		for(var key in data){
			var obj=data[key];
			var chartObj={};
			chartObj.xcode=obj[field_x];
			chartObj.ycode=obj[field_y];
			chartObj.zcode=obj[field_z];
			if(chartObj.zcode==undefined){
				chartObj.zcode=' ';
			}
			chartArray.push(chartObj);
		}
		if(this.chartType=='pie'){
			this.makePieChartData(chartArray);
		}else if(this.chartType=='combination'){
				this.makeCombinationChartData(chartArray);
		}else{
			this.makeChartData(chartArray);
		}
	}, this.showColumnChart = function(xArray,dataarray) {
		Highcharts.setOptions({
			  colors: ['#0000FF', '#800080', '#00FF00'] ,
				global : {
					useUTC : false
				// 开启UTC
				}
			 });
		var option={
		         chart: {
		        	    renderTo: this.chartid,
			            type: this.chartType,
			            margin: 75,
			            shadow: this.isShow3D,
			            options3d: {
			                enabled: this.isShow3D,
			                alpha: 10,
			                beta: 25,
			                depth: 70
			            }
			        },
			        title: {
			            text: this.title
			        },
			        subtitle: {
			            text: ' '
			        },
			        tooltip: {
			            useHTML: true,
			            crosshairs: true,
			            formatter: function() {
			           		 return  this.x+"<br><span style=width:200 >"+
			                    this.series.name+':&nbsp;&nbsp;</span><span>'+ this.y+'</span>';
			            }
			        },
			        credits:{//右下角的文本  
			     			 enabled: false  
			        },
			        plotOptions: {
			            column: {
			                depth: 25
			            }
			        },
			        exporting:{ 
	                  enabled:false //用来设置是否显示‘打印’,'导出'等功能按钮，不设置时默认为显示 
	                },
			        xAxis: {
			            categories: xArray,
			            type:this.xAxisType
			        },
			        yAxis: {
			             title: {
			                 text: this.titleY
			             },
			             min:0, // 定义最小值  
			             plotLines: [{
			                 value: 0,
			                 width: 1,
			                 color: '#808080'
			             }]
			         },
			        series:dataarray
			    };
		this.config(option);
		this.chartObj = new Highcharts.Chart(option);
		this.finish(this.chartObj);
	}, this.showPieChart = function(dataarray) {//显示饼图
		var option={
		         chart: {
		        	    renderTo: this.chartid,
			            plotBackgroundColor: null,
			            plotBorderWidth: null,
			            plotShadow: false,
			            shadow: this.isShow3D,
			            type: 'pie',
			            options3d: {
			                enabled: this.isShow3D,
			                alpha: 45,
			                beta: 0
			            }
			        },
			        title: {
			            text: this.title
			        },
			        tooltip: {
			    	    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
			        },
			        plotOptions: {
			            pie: {
			                allowPointSelect: true,
			                cursor: 'pointer',
			                depth:25,
			                dataLabels: {
			                    enabled: true,
			                    color: '#000000',
			                    connectorColor: '#000000',
			                    format: '<b>{point.name}</b>: {point.percentage:.1f} %'
			                }
			            }
			        },
			        credits:{//右下角的文本  
		     			 enabled: false  
			        },
			        exporting:{ 
			            enabled:false //用来设置是否显示‘打印’,'导出'等功能按钮，不设置时默认为显示 
			        },
			        series: [{
			            type: 'pie',
			            name: this.titleY,
			            data:dataarray
			        }]
			    };
		this.config(option);
		this.chartObj = new Highcharts.Chart(option);
		this.finish(this.chartObj);
	}, this.showCombinationChart = function(xArray,dataarray) {
		Highcharts.setOptions({
			  colors: ['#0000FF', '#800080', '#00FF00'] ,
				global : {
					useUTC : false
				// 开启UTC
				}
			 });
		var titleY=this.titleY;
		
		var option={
				 chart: {         
					 renderTo: this.chartid,
					 shadow: this.isShow3D,
					 options3d: {
			                enabled: this.isShow3D,
			                alpha: 45,
			                beta: 0
			            }
			        },                                                                
			        title: {                                                          
			            text: this.title                            
			        },                                                                
			        xAxis: {                                                          
			            categories:xArray,
			            type:this.xAxisType
			        },
			        yAxis: {
			             title: {
			                 text: this.titleY
			             },
			             min:0, // 定义最小值  
			             plotLines: [{
			                 value: 0,
			                 width: 1,
			                 color: '#808080'
			             }]
			         },
			        tooltip: {                                                        
			            formatter: function() {                                       
			                var s;                                                    
			                if (this.point.name) { // the pie chart                   
			                    s = ''+                                               
			                        this.point.name +': '+ this.y +' '+titleY;         
			                } else {                                                  
			                    s = ''+                                               
			                        this.x  +': '+ this.y;                            
			                }                                                         
			                return s;                                                 
			            }                                                             
			        },                                                                
			        labels: {                                                         
			            items: [{                                                     
			                html: '所有'+this.titleY+'总和',                          
			                style: {                                                  
			                    left: '40px',                                         
			                    top: '8px',                                           
			                    color: 'black'                                        
			                }                                                         
			            }]                                                            
			        }, credits:{//右下角的文本  
			     			 enabled: false  
			        },
			        exporting:{ 
	                  enabled:false //用来设置是否显示‘打印’,'导出'等功能按钮，不设置时默认为显示 
	                },                                                                
			        series:dataarray                                                             
			    };
			this.config(option);
			this.chartObj = new Highcharts.Chart(option);    
			this.finish(this.chartObj);
	}, this.makeChartData = function(data) {
		var map={};
		var xMap={};
		for(var key in data){ 
			var obj=data[key];
			xMap[obj.zcode]=obj.zcode;//类型
			map[obj.xcode]=obj.xcode;//x轴
		}
		var renArray=this.maptoArray(map);//x轴
		var operArray=this.maptoArray(xMap);
		var dataArray=[];
		for(var key in operArray){
			var type=operArray[key];
			var tdarray=new Array();
			for(var key1 in data){ 
				var obj=data[key1];
				var zcode=obj.zcode;
				if(type==zcode){
					tdarray.push([obj.xcode+'',parseInt(obj.ycode)]);
				}
			}
			var dataMap={
		            name: type,
		            data: tdarray
		        };
			dataArray.push(dataMap);
		}
		this.showColumnChart(renArray,dataArray);
	},this.maptoArray=function(map){
		var array=new Array();
		for(var key in map){ 
			array.push(key);
		}
		return array;
	},this.makePieChartData=function(data){
		var dataArray=[];
		for(var key in data){ 
			var obj=data[key];
			dataArray.push([obj.xcode,parseInt(obj.ycode)]);
		}
		this.showPieChart(dataArray);
	},this.makeCombinationChartData=function(data){
		var map={};
		var xMap={};
		for(var key in data){ 
			var obj=data[key];
			xMap[obj.zcode]=obj.zcode;//类型
			map[obj.xcode]=obj.xcode;//x轴
		}
		var renArray=this.maptoArray(map);//x轴
		var operArray=this.maptoArray(xMap);
		var dataArray=[];
		
		var pieArray=[];
		for(var key in operArray){
			var type=operArray[key];
			var tdarray=new Array();
			
			var number=0;
			for(var key1 in data){ 
				var obj=data[key1];
				var zcode=obj.zcode;
				if(type==zcode){
					tdarray.push([obj.xcode+'',parseInt(obj.ycode)]);
					number+=parseInt(obj.ycode);
				}
			}
			var dataMap={
		            name: type,
		            data: tdarray,
		            type: 'column'
		        };
			dataArray.push(dataMap);
			var nr=number;
			pieArray.push({name:type,y:nr,color: Highcharts.getOptions().colors[key]});
		}
		var lineArray=[];
		for(var key in renArray){
			var x=renArray[key];
			var number=0;
			var xindex=0;
			for(var key1 in data){ 
				var obj=data[key1];
				var tx=obj.xcode;
				if(tx==x){
					xindex++;
					number+=parseInt(obj.ycode);
				}
			}
			var nr=number;
			if(xindex>0){
				 nr=number/xindex;
				 nr=nr;
			}
			lineArray.push(nr);
		}
		//renArray dataArray lineArray  pieArray
		dataArray.push({                                                              
            type: 'pie',                                                  
            name: '总和 水平',                                    
            data: pieArray,                                                           
            center: [100, 80],                                            
            size: 100,                                                    
            showInLegend: false,                                          
            dataLabels: {                                                 
                enabled: false                                            
            }                                                             
        });
        dataArray.push({                                                              
            type: 'spline',                                               
            name: '平均',                                              
            data:lineArray,                               
            marker: {                                                     
            	lineWidth: 2,                                               
            	lineColor: Highcharts.getOptions().colors[3],               
            	fillColor: 'white'                                          
            }                                                             
        });
		this.showCombinationChart(renArray, dataArray);
	},this.finish=function(chart){
		if(this.success!=undefined){
			this.success(chart);
		}
	},this.config=function(option){
		if(this.execBeforeScript!=undefined){
			eval(this.execBeforeScript);
		}
		if(this.callBackConfig!=undefined){
			this.callBackConfig(option);
		}
	}
}



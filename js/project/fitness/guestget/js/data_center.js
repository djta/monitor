
var teamValues = [], teamLabels = [];
(function(){
	// tab切换
	$('.top-nav li').on('click', function(){
	    var id = this.dataset.id, activeCls = 'active';

	    this.classList.add(activeCls);
	    $(this).siblings('li').removeClass(activeCls);
	    $('.lists').hide();
	    $('#'+id).show();
	});

	var now = new Date, y = now.getFullYear(), m = now.getMonth()+1, d = now.getDate();

	// 日 tab 日期默认显示
	$("#dayDate").val(now.format('yyyy年MM月dd日'));
	$("#dayDate").calendar({
		dateFormat: 'yyyy年mm月dd日',
		value: [y+'/'+m +'/'+ d],
		onChange: function(p, values, displayValues){
			var date = new Date(parseInt(displayValues));  
		    var y = date.getFullYear();    
		    var m = date.getMonth() + 1;    
		    m = m < 10 ? ('0' + m) : m;    
		    var d = date.getDate();    
		    d = d < 10 ? ('0' + d) : d; 
			$("#daydateinfo").val(y+"-"+m+"-"+d);
			console.log(values, displayValues);
			searchdaytarget();
		}
	});

	// 月 tab 日期区间默认显示
	var s = getMonthStartDate(y, m-1), e = getMonthEndDate(y, m-1);
	$("#monthDate").val(s + '~' + e);

	var yearArr = [];
    var currentYear = new Date().getFullYear();
    for(var k = parseInt(currentYear) - 2; k <= parseInt(currentYear) + 7; k++ ){
    	yearArr[yearArr.length] = k;
    }
    console.log(yearArr);
	
	$("#monthDate").picker({
	  title: "请选择月份",
	  value: [y, m, d],
	  cols: [
	    {
	      textAlign: 'center',
	      values: yearArr
	      //如果你希望显示文案和实际值不同，可以在这里加一个displayValues: [.....]
	    },
	    {
	      textAlign: 'center',
	      values: ['1','2','3','4','5','6','7','8','9','10','11','12']
	    }
	  ],
	  onChange: function(p, values, displayValues){
			
			var start = getMonthStartDate(values[0],values[1]-1),
				end = getMonthEndDate(values[0],values[1]-1);
			
			searchmonthtarget();
			console.log(values)
			setTimeout(function(){
				$("#monthDate").val(start + '~' + end)
			})
		}
	})

	//获得本月的开始日期 
	function getMonthStartDate(nowyear,months){
	   var monthStartDate = new Date(nowyear, months, 1);
		$("#monthdateinfo").val(monthStartDate.format('yyyy-MM-dd'));
	   return formatDate(monthStartDate);//返回当月第一天
	}

	//获得本月的结束日期 
	function getMonthEndDate(nowyear,months){
	   var days= getMonthDays(nowyear,months);//获取当月总共有多少天
	   var monthEndDate = new Date(nowyear,months,days);
	   console.log(nowyear,months,days,'--')
	   return formatDate(monthEndDate);//返回当月结束时间
	}


	//格式化日期
	function formatDate(date){
	    var myyear = date.getFullYear();
	    var mymonth = date.getMonth() + 1;
	    var myweekday = date.getDate();

	    if (mymonth < 10) {
	       mymonth = "0" + mymonth;
	    }
	   	if (myweekday < 10) {
	      myweekday = "0" + myweekday;
	    }
	    // return (myyear + "年" + mymonth + "月" + myweekday + '日');
	    return (mymonth + "月" + myweekday + '日');
	}

	//获得某月的天数 
	function getMonthDays(nowyear,myMonth){
	    var monthStartDate = new Date(nowyear, myMonth, 1);
	    var monthEndDate = new Date(nowyear, myMonth + 1, 1);
	    var days = (monthEndDate - monthStartDate) / (1000 * 60 * 60 * 24);//格式转换
	    return days;
	}


	// 部门
	function getDepart($dom){
		$dom.picker({
			title: "请选择部门",
			cols: [{
				textAlign: 'center',
				values: teamLabels,
				//displayValues: teamLabels
				//如果你希望显示文案和实际值不同，可以在这里加一个displayValues: [.....]
			}],
			onChange: function(e, values, displayValues){
				console.log(values, displayValues);
				console.log(getDepartValue(values[0]));
				if($(".top-nav .active").attr("data-id")=="monthTab"){
					$("#monthteam").val(getDepartValue(values[0]));
					searchmonthtarget();
				}else if($(".top-nav .active").attr("data-id")=="dayTab"){
					$("#dayteam").val(getDepartValue(values[0]));
					searchdaytarget();
				}
			}
		});
	}
	
	function getDepartValue(teamname){
		var teamvalue = "";
		for(var i = 0; i < teamLabels.length; i++){
			if( teamname == teamLabels[i] ){
				teamvalue = teamValues[i];
				break;
			}
		}
		return teamvalue;
	}

	getDepart($("#departMonth"));
	getDepart($("#departWeek"));
	getDepart($("#departDay"));

	// 周 tab 日期区间默认显示
	var weekArr = []
	for( var w = 1; w <= 53; w++ ){
		weekArr.push('第'+ w +'周');
	}

	$("#weekDate").picker({
	  title: "请选择月份",
	  value: [y, m, d],
	  cols: [
	    {
	      textAlign: 'center',
	      values: yearArr
	      //如果你希望显示文案和实际值不同，可以在这里加一个displayValues: [.....]
	    },
	    {
	      textAlign: 'center',
	      values: weekArr
	    }
	  ],
	  onChange: function(p, values, displayValues){
	  }
	})
})();
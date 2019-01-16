/*** PT **/
/** 综合成交率 */
function posClass(){
	this.legendData = ['POS','P1','P2'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#posdaterangespan").html($("#posfdate").val() + " ~ " + $("#postdate").val());
		this.loadCharts($("#posfdate").val(), $("#postdate").val(), $("#posdatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=pos].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/pt/pos?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("pos", '1');
		});
	}
}
/** POS接待率 */
function receptClass(){
	this.legendData = ['POS接待率'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#receptdaterangespan").html($("#receptfdate").val() + " ~ " + $("#recepttdate").val());
		this.loadCharts($("#receptfdate").val(), $("#recepttdate").val(), $("#receptdatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=recept].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/pt/recept?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("recept", '1');
		});
	}
}
/*** 课时成交单价 */
function feeClass(){
	this.legendData = ['课时成交单价'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = false;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#feedaterangespan").html($("#feefdate").val() + " ~ " + $("#feetdate").val());
		this.loadCharts($("#feefdate").val(), $("#feetdate").val(), $("#feedatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=fee].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/pt/fee?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("fee", "");
		});
	}
}
/*** 首节课续费率 */
function cttnClass(){
	this.myChart;
	this.total = 0;
	this.legendData = [], seriesDataArr = [];
	this.titlename = "首节课续费";
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#cttndaterangespan").html($("#cttnfdate").val() + " ~ " + $("#cttntdate").val());
		this.loadCharts($("#cttnfdate").val(), $("#cttntdate").val(), $("#cttndatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=cttn].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/pt/cttn?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("cttn", "1");
		});
	}
}

/*** 首次课续费率 */
function firstcttn(){
	this.myChart;
	this.total = 0;
	this.legendData = [], seriesDataArr = [];
	this.titlename = "首次课续费率";
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#firstcttndaterangespan").html($("#firstcttnfdate").val() + " ~ " + $("#firstcttntdate").val());
		this.loadCharts($("#firstcttnfdate").val(), $("#firstcttntdate").val(), $("#firstcttndatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=firstcttn].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/pt/firstcttn?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("cttn", "1");
		});
	}
}

/*** 未成交原因指数 */
function failedClass(){
	this.myChart;
	this.total = 0;
	this.legendData = [], seriesDataArr = [];
	this.titlename = "未成交原因指数";
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#faileddaterangespan").html($("#failedfdate").val() + " ~ " + $("#failedtdate").val());
		this.loadCharts($("#failedfdate").val(), $("#failedtdate").val(), $("#faileddatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=failed].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/pt/failed?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("failed", "");
		});
	}
}

/** 平均授课量 */
function avgptClass(){
	this.legendData = ['平均授课量'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = false;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#avgptdaterangespan").html($("#avgptfdate").val() + " ~ " + $("#avgpttdate").val());
		this.loadCharts($("#avgptfdate").val(), $("#avgpttdate").val(), $("#avgptdatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=avgpt].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/pt/avgpt?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("avgpt", "");
		});
	}
}

/** 耗课率 */
function ptClass(){
	this.myChart;
	this.total = 0;
	this.legendData = [], seriesDataArr = [];
	this.titlename = "耗课率";
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#ptdaterangespan").html($("#ptfdate").val() + " ~ " + $("#pttdate").val());
		this.loadCharts($("#ptfdate").val(), $("#pttdate").val(), $("#ptdatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=pt].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/pt/pt?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("pt", "");
		});
	}
}

/** 员工任务达标率 */
function taskClass(){
	this.myChart;
	this.total = 0;
	this.legendData = ['员工任务达标率'], seriesDataArr = [];
	this.titlename = "员工任务达标率";
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#taskmonthspan").html($("#taskmonth").val());
		this.loadCharts($("#taskmonth").val());
	},
	/** 加载数据 */
	this.loadCharts = function(yearmonth){
		var compareflag = $("li[data-name=compareflag][data-area=task].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var tmp = yearmonth.split("-");
		var year = parseInt(tmp[0]);
		var month = parseInt(tmp[1]);
		var uri = "/action/project/fitness/report/analysis/org/loaddata/pt/task?year="+year+"&month="+month+"&datatype=&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("task", "");
		});
	}
}

/** MC **/
/** 综合到访量 */
function visitClass(){
	this.legendData = ['陌生到访量','平均到访量','预约到访量'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#visitdaterangespan").html($("#visitfdate").val() + " ~ " + $("#visittdate").val());
		this.loadCharts($("#visitfdate").val(), $("#visittdate").val(), $("#visitdatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=visit].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/mc/visit?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("visit", "");
		});
	}
}

/** 综合成交率 */
function inclubClass(){
	this.legendData = ['预约成交率','陌生成交率','综合成交率'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#inclubdaterangespan").html($("#inclubfdate").val() + " ~ " + $("#inclubtdate").val());
		this.loadCharts($("#inclubfdate").val(), $("#inclubtdate").val(), $("#inclubdatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=inclub].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/mc/inclub?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("inclub", "1");
		});
	}
}

/** 产品均价 */
function avgcardClass(){
	this.legendData = ['产品均价'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = false;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#avgcarddaterangespan").html($("#avgcardfdate").val() + " ~ " + $("#avgcardtdate").val());
		this.loadCharts($("#avgcardfdate").val(), $("#avgcardtdate").val(), $("#avgcarddatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=avgcard].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/mc/avgcard?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("avgcard", "");
		});
	}
}

/** 续费率 */
function cttncardClass(){
	this.legendData = ['续费率'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = false;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#cttncarddaterangespan").html($("#cttncardfdate").val() + " ~ " + $("#cttncardtdate").val());
		this.loadCharts($("#cttncardfdate").val(), $("#cttncardtdate").val(), $("#cttncarddatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=cttncard].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/mc/cttncard?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("cttncard", "1");
		});
	}
}

/** 渠道占比指数 */
function sourceClass(){
	this.myChart;
	this.total = 0;
	this.legendData = [], seriesDataArr = [];
	this.titlename = "渠道占比指数";
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#sourcedaterangespan").html($("#sourcefdate").val() + " ~ " + $("#sourcetdate").val());
		this.loadCharts($("#sourcefdate").val(), $("#sourcetdate").val(), $("#sourcedatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=source].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/mc/source?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("source", "1");
		});
	}
}

/** 渠道未成交占比 */
function guestsourceClass(){
	this.myChart;
	this.total = 0;
	this.legendData = [], seriesDataArr = [];
	this.titlename = "渠道未成交占比";
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#guestsourcedaterangespan").html($("#guestsourcefdate").val() + " ~ " + $("#guestsourcetdate").val());
		this.loadCharts($("#guestsourcefdate").val(), $("#guestsourcetdate").val(), $("#guestsourcedatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=guestsource].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/mc/guestsource?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("guestsource", "1");
		});
	}
}

/** 未成交原因指数 */
function guestreasonClass(){
	this.myChart;
	this.total = 0;
	this.legendData = [], seriesDataArr = [];
	this.titlename = "未成交原因指数";
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#guestreasondaterangespan").html($("#guestreasonfdate").val() + " ~ " + $("#guestreasontdate").val());
		this.loadCharts($("#guestreasonfdate").val(), $("#guestreasontdate").val(), $("#guestreasondatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=guestreason].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/mc/guestreason?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("guestreason", "1");
		});
	}
}

/** 未续费原因指数 */
function custreasonClass(){
	this.myChart;
	this.total = 0;
	this.legendData = [], seriesDataArr = [];
	this.titlename = "未续费原因指数";
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#custreasondaterangespan").html($("#custreasonfdate").val() + " ~ " + $("#custreasontdate").val());
		this.loadCharts($("#custreasonfdate").val(), $("#custreasontdate").val(), $("#custreasondatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=custreason].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/mc/custreason?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("custreason", "1");
		});
	}
}

/** 员工任务达标率 */
function mctaskClass(){
	this.myChart;
	this.total = 0;
	this.legendData = ['员工任务达标率'], seriesDataArr = [];
	this.titlename = "员工任务达标率";
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#mctaskmonthspan").html($("#mctaskmonth").val());
		this.loadCharts($("#mctaskmonth").val());
	},
	/** 加载数据 */
	this.loadCharts = function(yearmonth){
		var compareflag = $("li[data-name=compareflag][data-area=mctask].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var tmp = yearmonth.split("-");
		var year = parseInt(tmp[0]);
		var month = parseInt(tmp[1]);
		var uri = "/action/project/fitness/report/analysis/org/loaddata/mc/task?year="+year+"&month="+month+"&datatype=&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("mctask", "");
		});
	}
}

/** 产品销量占比 */
function cardtypeClass(){
	this.myChart;
	this.total = 0;
	this.legendData = [], seriesDataArr = [];
	this.titlename = "产品销量占比";
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#cardtypedaterangespan").html($("#cardtypefdate").val() + " ~ " + $("#cardtypetdate").val());
		this.loadCharts($("#cardtypefdate").val(), $("#cardtypetdate").val(), $("#cardtypedatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=cardtype].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/org/loaddata/mc/cardtype?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("cardtype", "1");
		});
	}
}

/*** 未续费会籍客户 */
function noRenewal(){
	this.legendData = ['未续费会籍客户'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#norenewaldaterangespan").html($("#norenewalfdate").val() + " ~ " + $("#norenewaltdate").val());
		this.loadCharts($("#norenewalfdate").val(), $("#norenewaltdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=norenewal].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/report/analysis/visit/loaddata/norenewal?fdate="+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("norenewal", "");
		});
	}
}

/*** 俱乐部数据预览-》每日营业收入 */
function operatingincome(){
	this.legendData = ['会员卡销售','私教销售','运营收入','零售收入'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#operatingincomedaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/clubdatapreview/loaddata/operatingincome?fdate="+fdate+"&tdate="
		+tdate+"&datatype="+datatype+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#operatingincomedaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("operatingincome", "0");
		});
	}
}

/*** 俱乐部数据预览-》综合到访量 */
function comprehensiveclosing(){
	this.legendData = ['陌生到访量','预约到访量'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#comprehensiveclosingdaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/clubdatapreview/loaddata/comprehensiveclosing?fdate="+fdate+"&tdate="
		+tdate+"&datatype="+datatype+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#comprehensiveclosingdaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("comprehensiveclosing", "");
		});
	}
}

/*** 俱乐部数据预览-》综合成交率(POS、P1、P2) */
function comprehensiveclosingtype(){
	this.legendData = ['POS','P1','P2'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#comprehensiveclosingtypedaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/clubdatapreview/loaddata/comprehensiveclosingtype?fdate="+fdate+"&tdate="
		+tdate+"&datatype="+datatype+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#comprehensiveclosingtypedaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("comprehensiveclosingtype", "1");
		});
	}
}

/*** 俱乐部数据预览-》合同欠款 */
function contractbalance(){
	this.legendData = ['合同欠款'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#contractbalancedaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/clubdatapreview/loaddata/contractbalance?fdate="+fdate+"&tdate="
		+tdate+"&datatype="+datatype+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#contractbalancedaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("contractbalance", "");
		});
	}
}

/*** 会籍销售预览-》销售金额 */
function salesamount(){
	this.legendData = ['销售金额'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#salesamountdaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/mcdatapreview/loaddata/salesamount?fdate="+fdate+"&tdate="+tdate
		+"&datatype="+datatype+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#salesamountdaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("salesamount", "");
		});
	}
}

/*** 会籍销售预览-》综合成交率 */
function mccomprehensiveclosing(){
	this.legendData = ['预约成交率','陌生成交率','综合成交率'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#mccomprehensiveclosingdaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/mcdatapreview/loaddata/mccomprehensiveclosing?fdate="+fdate
		+"&tdate="+tdate+"&datatype="+datatype+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#mccomprehensiveclosingdaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("mccomprehensiveclosing", "1");
		});
	}
}

/*** 会籍销售预览-》产品均价 */
function productprice(){
	this.legendData = ['产品均价'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#productpricedaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/mcdatapreview/loaddata/productprice?fdate="+fdate+"&tdate="
		+tdate+"&datatype="+datatype+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#productpricedaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("productprice", "");
		});
	}
}

/*** 会籍销售预览-》续费率 */
function renewalrate(){
	this.legendData = ['续费率','环比'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#renewalratedaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/mcdatapreview/loaddata/renewalrate?fdate="+fdate+"&tdate="
		+tdate+"&datatype="+datatype+"&compareflag=H"+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#renewalratedaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("renewalrate", "1");
		});
	}
}
/*** 私教销售预览-》销售金额 */
function ptsalesamount(){
	this.legendData = ['销售金额'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#ptsalesamountdaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/ptdatapreview/loaddata/ptsalesamount?fdate="+fdate+"&tdate="
		+tdate+"&datatype="+datatype+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#ptsalesamountdaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("ptsalesamount", "");
		});
	}
}
/*** 访客数据预览-》客流量 */
function traffic(){
	this.legendData = ['客流量'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#trafficdaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/visitors/loaddata/traffic?fdate="+fdate+"&tdate="
		+tdate+"&datatype="+datatype+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#trafficdaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("traffic", "");
		});
	}
}

/*** 访客数据预览-》会员入场数 */
function custentrancenum(){
	this.legendData = ['会员入场数'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#custentrancenumdaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/visitors/loaddata/custentrancenum?fdate="+fdate+"&tdate="
		+tdate+"&datatype="+datatype+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#custentrancenumdaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("custentrancenum", "");
		});
	}
}

/*** 访客数据预览-》团操上课数 */
function classnum(){
	this.legendData = ['团操上课数'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#classnumdaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/visitors/loaddata/classnum?fdate="+fdate+"&tdate="+tdate
		+"&datatype="+datatype+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#classnumdaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("classnum", "");
		});
	}
}

/*** 访客数据预览-》团操上课数 */
function sourcevisitors(){
	this.legendData = ['BR','WI','DI'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#sourcevisitorsdaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/visitors/loaddata/sourcevisitors?fdate="+fdate+"&tdate="
		+tdate+"&datatype="+datatype+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#sourcevisitorsdaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("sourcevisitors", "");
		});
	}
}

/*** 营收汇总-》营收额 */
function revenuesummary(){
	this.legendData = ['营收额'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		$("#revenuesummarydaterangespick").html($("#fdate").val() + " ~ " + $("#tdate").val());
		this.loadCharts($("#fdate").val(), $("#tdate").val(), $("#datatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var uri = "/action/project/fitness/union/revenuesummary/loaddata/revenue?fdate="+fdate+"&tdate="
		+tdate+"&datatype="+datatype+"&org_id="+$("#org_id").val();
		getAjaxCall(uri, false, function(){
			$("#revenuesummarydaterangespan").html($("#fdate").val() + " ~ " + $("#tdate").val());
			bindChangeMagicType("revenuesummary", "");
		});
	}
}

/*** 入场 */
function custInleft(){
	this.legendData = ['入场统计'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		this.loadCharts($("#inleftfdate").val(), $("#inlefttdate").val(), $("#custcode").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, custcode){
		var uri = "/action/project/fitness/guest/detail/gustmenu/admissionRecord/inleft?fdate="
			+fdate+"&tdate="+tdate+"&custcode="+custcode;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("inleft", "");
		});
	}
}

/*** 平均入场时间 */
function custAverageMin(){
	this.legendData = ['平均入场时间'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		this.loadCharts($("#inleftfdate").val(), $("#inlefttdate").val(), $("#custcode").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, custcode){
		var uri = "/action/project/fitness/guest/detail/gustmenu/admissionRecord/averagemin?fdate="
			+fdate+"&tdate="+tdate+"&custcode="+custcode;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("averagemin", "");
		});
	}
}

/*** 私教授课统计 **/
function PtTeachingStatistical(){
	this.legendData = ['预约中','已签课','已取消','爽约'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		this.loadCharts($("#ptteachingstatisticalfdate").val(), $("#ptteachingstatisticaltdate").val(), $("#ptteachingstatisticaldatatype").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, datatype){
		var compareflag = $("li[data-name=compareflag][data-area=ptteachingstatistical].baobiao-active").attr("data-code");
		compareflag = (undefined != compareflag ? compareflag : "");
		var uri = "/action/project/fitness/class/private/privateteach/months/ptteachingstatistical?fdate="
			+fdate+"&tdate="+tdate+"&datatype="+datatype+"&compareflag="+compareflag;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("ptteachingstatistical", '');
		});
	}
}


/*** 上课频率趋势分析 */
function classFrequency(){
	this.legendData = ['上课频率趋势分析'];
	this.myChart;
	this.total = 0;
	this.isLegendShow = true;
	this.xdataArr = [], seriesDataArr = [];
	this.init = function(){
	},
	/** 更新日期 */
	this.loadData = function(){
		this.loadCharts($("#classfrequencyfdate").val(), $("#classfrequencytdate").val(), $("#custcode").val());
	},
	/** 加载数据 */
	this.loadCharts = function(fdate, tdate, custcode){
		var uri = "/action/project/fitness/guest/detail/gustmenu/reservationdetail/classfrequency?fdate="
			+fdate+"&tdate="+tdate+"&custcode="+custcode;
		getAjaxCall(uri, false, function(){
			bindChangeMagicType("classfrequency", "");
		});
	}
}



/*** 视图模式切换绑定 */
function bindChangeMagicType(dataArea, percentage){
	var obj;
	if( "pos" == dataArea ){	/** 综合成交率 */
		obj = posCharts;
	}else if( "recept" == dataArea ){	/** POS接待率 */
		obj = receptCharts;
	}else if( "fee" == dataArea ){	/** 课时成交单价 */
		obj = feeCharts;
	}else if( "cttn" == dataArea ){	/*** 首节课续费率 */
		obj = cttnCharts;
	}else if( "failed" == dataArea ){	/*** 未成交原因指数 */
		obj = failedCharts;
	}else if( "avgpt" == dataArea ){	/*** 平均授课量 */
		obj = avgptCharts;
	}else if( "pt" == dataArea ){	/** 耗课率 */
		obj = ptCharts;
	}else if( "task" == dataArea ){	/** 任务达标率 */
		obj = taskCharts;
	}else if( "visit" == dataArea ){	/** 综合到访量 */
		obj = visitCharts;
	}else if( "inclub" == dataArea ){	/** 综合成交率 */
		obj = inclubCharts;
	}else if( "avgcard" == dataArea ){	/** 产品均价 */
		obj = avgcardCharts;
	}else if( "cttncard" == dataArea ){	/** 续费率 */
		obj = cttncardCharts;
	}else if( "source" == dataArea ){	/** 渠道占比指数 */
		obj = sourceCharts;
	}else if( "guestsource" == dataArea ){	/** 渠道未成交占比 */
		obj = guestsourceCharts;
	}else if( "mctask" == dataArea ){	/**  员工任务达标率 */
		obj = mctaskCharts;
	}else if( "cardtype" == dataArea ){	/** 产品销量占比 */
		obj = cardtypeCharts;
	}else if( "norenewal" == dataArea ){	/** 未续费会籍客户 */
		obj = norw;
	}else if( "operatingincome" == dataArea ){	/***俱乐部数据预览-》 每日营业收入 */
		obj = income;
	}else if( "comprehensiveclosing" == dataArea ){	/*** 俱乐部数据预览-》综合到访量 */
		obj = closing;
	}else if( "comprehensiveclosingtype" == dataArea ){	/***俱乐部数据预览-》 综合成交率(POS、P1、P2) */
		obj = closingtype;
	}else if( "contractbalance" == dataArea ){	/***俱乐部数据预览-》 合同欠款 */
		obj = contract;
	}else if( "salesamount" == dataArea ){	/***会籍销售预览-》销售金额 */
		obj = sales;
	}else if( "mccomprehensiveclosing" == dataArea ){	/***会籍销售预览-》综合成交率 */
		obj = mccomprehensive;
	}else if( "productprice" == dataArea ){	/***会籍销售预览-》产品均价 */
		obj = price;
	}else if( "renewalrate" == dataArea ){	/***会籍销售预览-》续费率 */
		obj = rate;
	}else if( "ptsalesamount" == dataArea ){	/***私教销售预览-》销售金额 */
		obj = ptsales;
	}else if( "traffic" == dataArea ){	/***访客数据预览-》客流量 */
		obj = trafficnum;
	}else if( "custentrancenum" == dataArea ){	/*** 访客数据预览-》会员入场数 */
		obj = custnum;
	}else if( "classnum" == dataArea ){	/*** 访客数据预览-》团操上课数 */
		obj = cnum;
	}else if( "sourcevisitors" == dataArea ){	/*** 访客数据预览-》访客来源 */
		obj = source;
	}else if( "revenuesummary" == dataArea ){	/*** 营收汇总-》营收额 */
		obj = revenue;
	}else if( "inleft" == dataArea ){	/*** 入场记录 */
		obj = inleft;
	}else if( "averagemin" == dataArea ){	/*** 平均入场时间 */
		obj = averagemin;
	}else if( "ptteachingstatistical" == dataArea ){	/*** 私教授课统计 */
		obj = teachingStatistical;
	}else if( "classfrequency" == dataArea ){	/*** 上课频率趋势分析 */
		obj = frequency;
	}

	// 视图模式
	$("button[name=changeMagicType][data-area="+dataArea+"].active").removeClass("active");
	$("button[name=changeMagicType][data-area="+dataArea+"][data-type=line]").addClass("active");
	$("button[name=changeMagicType][data-area="+dataArea+"]").unbind().on("click",function(){
		$(this).siblings(".active").removeClass("active");
		$(this).addClass("active");
		if(percentage=="1"){
			changeMagicTypePercentage(obj, dataArea+"Div", $(this).data("type"));
		}else{
			changeMagicType(obj, dataArea+"Div", $(this).data("type"));
		}
	});
	// 按天、按周、按月
	$("select[data-name=datatype][data-area="+dataArea+"]").unbind().on("change",function(){
		obj.loadCharts($("#"+dataArea+"fdate").val(), $("#"+dataArea+"tdate").val(), $(this).val());
	});
}
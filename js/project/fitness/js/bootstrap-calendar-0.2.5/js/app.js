var calendar;
(function($) {
	"use strict";
	var _curDay;
	if( typeof(_currentDate) == "undefined" || undefined == _currentDate || "" == _currentDate ) {
		var curDate = new Date();
		_curDay = curDate.getFullYear() + "-" + curDate.getMonthFormatted() + "-" + curDate.getDateFormatted();
	}else{
		_curDay = _currentDate;
	}
	var options = {
		events_source: contextPath+'/js/bootstrap/js/bootstrap-calendar-0.2.5/events.json.php',
		view: 'month',
		language: 'zh-CN',
		tmpl_path: contextPath+'/js/bootstrap/js/bootstrap-calendar-0.2.5/tmpls/',
		tmpl_cache: false,
		day: _curDay,
		onAfterEventsLoad: function(events) {
			$('.page-header').text(this.getTitle());
			
		},
		onAfterViewLoad: function(view) {
			$('.page-header h3').text(this.getTitle());
			$('.btn-group button').removeClass('active');
			$('button[data-calendar-view="' + view + '"]').addClass('active');
		},
		classes: {
			months: {
				general: 'label'
			}
		}
	};

	calendar = $('#calendar').calendar(options);
	calendar.setLanguage('zh-CN');
	calendar.view();
	/* 取消双击默认绑定的事件 */
	$(".cal-cell1,.pull-right").unbind().on("dbclick",function(){});
	/* 取消选中行时在左侧显示周几 */
	$('.cal-month-box .cal-row-fluid').unbind('mouseenter');
	
	/*
	$('.btn-group button[data-calendar-nav]').each(function() {
		var $this = $(this);
		$this.click(function() {
			calendar.navigate($this.data('calendar-nav'));
		});
	});

	$('.btn-group button[data-calendar-view]').each(function() {
		var $this = $(this);
		$this.click(function() {
			calendar.view($this.data('calendar-view'));
		});
	});

	$('#first_day').change(function(){
		var value = $(this).val();
		value = value.length ? parseInt(value) : null;
		calendar.setOptions({first_day: value});
		calendar.view();
	});

	$('#language').change(function(){
		calendar.setLanguage($(this).val());
		calendar.view();
	});

	$('#events-in-modal').change(function(){
		var val = $(this).is(':checked') ? $(this).val() : null;
		calendar.setOptions({modal: val});
	});
	$('#format-12-hours').change(function(){
		var val = $(this).is(':checked') ? true : false;
		calendar.setOptions({format12: val});
		calendar.view();
	});
	$('#show_wbn').change(function(){
		var val = $(this).is(':checked') ? true : false;
		calendar.setOptions({display_week_numbers: val});
		calendar.view();
	});
	$('#show_wb').change(function(){
		var val = $(this).is(':checked') ? true : false;
		calendar.setOptions({weekbox: val});
		calendar.view();
	});*/
	$('#events-modal .modal-header, #events-modal .modal-footer').click(function(e){
		//e.preventDefault();
		//e.stopPropagation();
	});
	
}(jQuery));

function switchmonth(){
	"use strict";
	var _curDay;
	if( typeof(_currentDate) == "undefined" || undefined == _currentDate || "" == _currentDate ) {
		var curDate = new Date($('#datemonth').html());
		_curDay = curDate.getFullYear() + "-" + curDate.getMonthFormatted() + "-" + curDate.getDateFormatted();
	}else{
		_curDay = _currentDate;
	}
	var options = {
		events_source: contextPath+'/js/bootstrap/js/bootstrap-calendar-0.2.5/events.json.php',
		view: 'month',
		language: 'zh-CN',
		tmpl_path: contextPath+'/js/bootstrap/js/bootstrap-calendar-0.2.5/tmpls/',
		tmpl_cache: false,
		day: _curDay,
		onAfterEventsLoad: function(events) {
			$('.page-header').text(this.getTitle());
			
		},
		onAfterViewLoad: function(view) {
			$('.page-header h3').text(this.getTitle());
			$('.btn-group button').removeClass('active');
			$('button[data-calendar-view="' + view + '"]').addClass('active');
		},
		classes: {
			months: {
				general: 'label'
			}
		}
	};

	calendar = $('#calendar').calendar(options);
	calendar.setLanguage('zh-CN');
	calendar.view();
	/* 取消双击默认绑定的事件 */
	$(".cal-cell1,.pull-right").unbind().on("dbclick",function(){});
	/* 取消选中行时在左侧显示周几 */
	$('.cal-month-box .cal-row-fluid').unbind('mouseenter');
	
	/*
	$('.btn-group button[data-calendar-nav]').each(function() {
		var $this = $(this);
		$this.click(function() {
			calendar.navigate($this.data('calendar-nav'));
		});
	});

	$('.btn-group button[data-calendar-view]').each(function() {
		var $this = $(this);
		$this.click(function() {
			calendar.view($this.data('calendar-view'));
		});
	});

	$('#first_day').change(function(){
		var value = $(this).val();
		value = value.length ? parseInt(value) : null;
		calendar.setOptions({first_day: value});
		calendar.view();
	});

	$('#language').change(function(){
		calendar.setLanguage($(this).val());
		calendar.view();
	});

	$('#events-in-modal').change(function(){
		var val = $(this).is(':checked') ? $(this).val() : null;
		calendar.setOptions({modal: val});
	});
	$('#format-12-hours').change(function(){
		var val = $(this).is(':checked') ? true : false;
		calendar.setOptions({format12: val});
		calendar.view();
	});
	$('#show_wbn').change(function(){
		var val = $(this).is(':checked') ? true : false;
		calendar.setOptions({display_week_numbers: val});
		calendar.view();
	});
	$('#show_wb').change(function(){
		var val = $(this).is(':checked') ? true : false;
		calendar.setOptions({weekbox: val});
		calendar.view();
	});*/
	$('#events-modal .modal-header, #events-modal .modal-footer').click(function(e){
		//e.preventDefault();
		//e.stopPropagation();
	});
}
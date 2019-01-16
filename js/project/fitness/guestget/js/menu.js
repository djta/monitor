(function(){
    // 筛选条件 开始时间
    $("#startdate").calendar({
    	value: [new Date().format("yyyy-MM-01")],
        dateFormat: 'yyyy/mm/dd',
        onChange: function (p, values, displayValues) {
          console.log(values, displayValues);
        }
     });
    // 结束时间
    $("#enddate").calendar({
    	value: [$("#enddate").val()],
        dateFormat: 'yyyy/mm/dd',
        onChange: function (p, values, displayValues) {
          console.log(values, displayValues);
        }
     });

    $('.js-filter-btn').on('click', function(){
    	$('.gg-filter-dlg').show();
    	$('.gg-mask').show();
    })

    $('.filter-dlg-close').on('click', function(){
    	$('.gg-filter-dlg').hide();
    	$('.gg-mask').hide();
    });

    // 重置
    $('.reset-btn').on('click', function(){
        $('form input[type="checkbox"]').attr('checked', false)
    })


})();
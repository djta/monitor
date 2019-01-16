(function(){
	// tab切换
	$('.top-nav li').on('click', function(){
	    var id = this.dataset.id, activeCls = 'active';

	    this.classList.add(activeCls);
	    $(this).siblings('li').removeClass(activeCls);
	    console.log($(this).siblings('li'));
	    $('.lists').hide();
	    $('#'+id).show();
	})

    $('#noticeList li').on('click', function(){
        $('.gg-notice-dlg').show();
        $('.gg-mask').show();
    });

    $('.notice-dlg-close').on('click', function(){
        $('.gg-notice-dlg').hide();
        $('.gg-mask').hide();
    });
    
    $('.notice-dlg-close').on('click', function(){
        $('.gg-notice-dlg').hide();
        $('.gg-mask').hide();
    });
    
})();
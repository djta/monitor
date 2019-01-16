(function(){
	$('.js-level').on('click', function(){
		$('.level-list').show();
	});
	$('.icon-setting').on('click', function(){
		$('.setting-list').show();
	});

	$('.level-list').on('click', 'li', function(){
		var color = this.dataset.color, $dot = $('#dot');

		$dot.hasClass('red') && $dot.removeClass('red');
		$dot.hasClass('yellow') && $dot.removeClass('yellow');
		$dot.hasClass('green') && $dot.removeClass('green');
		
		$dot.addClass(color);
		$('.level-list').hide();
		console.log(color)
	})
	$('.setting-list').on('click', 'li', function(){
		$('.setting-list').hide();
	})
})();
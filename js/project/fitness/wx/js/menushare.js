/** 必须在jweixin-1.2.0.js之后引入 */
function menuShareConfig(opts){
	// 分享到朋友圈
	wx.onMenuShareTimeline({
		title: opts.titlename,
		link: opts.linkuri,
		imgUrl: opts.imguri,
		success: function () {
			// 用户确认分享后执行的回调函数
			if( "undefined" != typeof(opts.callback) && null != opts.callback ){
				opts.callback();
			}
			alert('分享到朋友圈成功');
		},
		cancel: function () {
			// 用户取消分享后执行的回调函数
		}
	});
	// 分享给朋友
	wx.onMenuShareAppMessage({
		title: opts.titlename,
		desc: opts.desc, // 分享描述
		link: opts.linkuri,
		imgUrl: opts.imguri,
		type: 'link', // 分享类型,music、video或link，不填默认为link
		dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
		success: function () {
			// 用户确认分享后执行的回调函数
			if( "undefined" != typeof(opts.callback) && undefined != opts.callback ){
				opts.callback();
			}
			alert('分享成功');
		},
		cancel: function () {
			// 用户取消分享后执行的回调函数
		}
	});
	// 分享到QQ
	wx.onMenuShareQQ({
		title: opts.titlename,
		desc: opts.desc, // 分享描述
		link: opts.linkuri,
		imgUrl: opts.imguri,
		success: function () {
			// 用户确认分享后执行的回调函数
			if( "undefined" != typeof(opts.callback) && undefined != opts.callback ){
				opts.callback();
			}
			alert('分享成功');
		},
		cancel: function () {
			// 用户取消分享后执行的回调函数
		}
	});
	// 分享到腾讯微博
	wx.onMenuShareWeibo({
		title: opts.titlename,
		desc: opts.desc, // 分享描述
		link: opts.linkuri,
		imgUrl: opts.imguri,
		success: function () {
			// 用户确认分享后执行的回调函数
			if( "undefined" != typeof(opts.callback) && undefined != opts.callback ){
				opts.callback();
			}
			alert('分享成功');
		},
		cancel: function () {
			// 用户取消分享后执行的回调函数
		}
	});
	// 分享到QQ空间
	wx.onMenuShareQZone({
		title: opts.titlename,
		desc: opts.desc, // 分享描述
		link: opts.linkuri,
		imgUrl: opts.imguri,
		success: function () {
			// 用户确认分享后执行的回调函数
			if( "undefined" != typeof(opts.callback) && undefined != opts.callback ){
				opts.callback();
			}
			alert('分享成功');
		},
		cancel: function () {
			// 用户取消分享后执行的回调函数
		}
	});
}
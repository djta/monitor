// $(window).scroll(function () {
//   var r = $('#selfNav')
//     var e = $(window).scrollTop(),
//       t = r.css('top'),
//       n = 0
//   if (e >= 248) { parseInt(t) >= 0 ? n = 0 : (n = (e - 248) / 3 - 80) >= 0 && (n = 0) } else {
//       if (t == '-80px') { return }
//       (n = -(248 - e) / 2) <= -80 && (n = -80)
//     }
//   r.css('top', n + 'px')
// })
var s = $('#toTop')
s.off().on('click', function () {
  $('body,html').animate({
    scrollTop: 0
  }, 500)
})

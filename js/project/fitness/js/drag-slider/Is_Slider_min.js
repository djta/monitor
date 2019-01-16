(function (w) {
    let iss = function (c) {
        this.g = c.groove;
        this.bk = c.block;
        this.se = c.single;
        this.pt = c.prevent;
        this.ct = 0;
        this.it = 0;
        this.sck = c.springback ? c.springback : false;
        this.pon = c.direction === "right" ? "left" : "right";
        return this.init()
    };
    iss.prototype = {
        Pc: function () {
            let userAgentInfo = navigator.userAgent;
            let list = ["Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod"];
            let b = true;
            for (let v = 0; v < list.length; v++) {
                if (userAgentInfo.indexOf(list[v]) > 0) {
                    b = false;
                    break
                }
            }
            return b
        },
        Func: function (f) {
            return typeof f === "function"
        },
        gle: function (obj, attribute) {
            return obj.currentStyle ? obj.currentStyle[attribute] : document.defaultView.getComputedStyle(obj, false)[attribute]
        },
        iie: function (m, n, t, fn, callback, iscall) {
            let ct = m;
            if (m > n) {
                let stime = setInterval(function () {
                    m -= (ct - n) / t * 10;
                    fn(m);
                    if (m <= n) {
                        m = n;
                        fn(m);
                        clearInterval(stime);
                        stime = null;
                        callback ? callback.bind(iscall)() : null
                    }
                }, 10)
            } else {
                let mtime = setInterval(function () {
                    m += (n - ct) / t * 10;
                    fn(m);
                    if (m >= n) {
                        m = n;
                        fn(m);
                        clearInterval(mtime);
                        mtime = null;
                        callback ? callback.bind(iscall)() : null
                    }
                }, 10)
            }
        },
        Cal: function (mun) {
            this.data.Result = false;
            this.ct = this.Original + this.it - mun;
            let sss = Number(this.gle(this.g, "width").replace("px", "")) - Number(this.gle(this.bk, "width").replace("px", ""));
            if (this.pon === "left") {
                if (this.ct >= 0) {
                    this.ct = 0
                } else {
                    if (this.ct <= -sss) {
                        this.ct = -sss;
                        this.data.Result = true
                    }
                }
            } else {
                if (this.ct >= sss) {
                    this.ct = sss;
                    this.data.Result = true
                } else {
                    if (this.ct <= 0) {
                        this.ct = 0
                    }
                }
            }
            this.data.pon = Math.abs(this.ct);
            this.iv(this.pon === "left" ? -this.ct : this.ct)
        },
        init: function () {
            this.g.style.position = "relative";
            this.bk.style.position = "absolute";
            this.bk.style.top = 0;
            this.bk.style[this.pon] = 0;
            this.data = {
                pon: "",
                Result: ""
            };
            if (this.Pc()) {
                this.pr()
            } else {
                this.mt()
            }
            return this
        },
        mt: function () {
            if (('ontouchstart' in window) || window.DocumentTouch && document instanceof DocumentTouch) {
                this.bk.addEventListener("touchstart", function (event) {
                    this.Original = event.changedTouches[0].clientX;
                    this.iin()
                }.bind(this));
                this.bk.addEventListener("touchmove", function (event) {
                    this.Cal(event.changedTouches[0].clientX)
                }.bind(this));
                this.bk.addEventListener("touchend", function (event) {
                    this.Cal(event.changedTouches[0].clientX);
                    this.so();
                    this.IsUp()
                }.bind(this))
            } else {
                this.pr()
            }
        },
        so: function () {
            this.it = this.ct;
            if (this.sck && !this.data.Result) {
                this.iie(this.ct, 0, 300, function (m) {
                    this.iv(this.pon === "left" ? -m : m)
                }.bind(this), function () {
                    this.ct = 0;
                    this.data.pon = 0;
                    this.it = 0;
                    this.Original = 0
                }, this)
            }
            this.IsUp()
        },
        pso: function () {
            this.so();
            window.onmousemove = this.g.onmouseout = window.onmouseup = undefined
        },
        pr: function () {
            this.bk.onmousedown = function (event) {
                this.Original = event.clientX;
                this.iin();
                window.onmousemove = function (event) {
                    this.Cal(event.clientX)
                }.bind(this);
                this.g.onmouseout = function () {
                    if (this.pt) {
                        this.pso.bind(this)()
                    }
                }.bind(this);
                window.onmouseup = this.pso.bind(this)
            }.bind(this)
        },
        iv: function (m) {
            if (this.data.Result && !this.se) {
                this.ius()
            }
            this.bk.style[this.pon] = m + "px";
            this.Func(this.move) ? this.move(this.data) : null
        },
        iin: function () {
            this.Func(this.down) ? this.down(this.data) : null
        },
        IsUp: function () {
            if (this.data.Result) {
                this.ius()
            }
            this.Func(this.up) ? this.up(this.data) : null
        },
        ius: function () {
            this.Func(this.success) ? this.success(this.data) : null
        }
    };
    return w.Is_Slider = function (c) {
        return new iss(c)
    }
})(window);
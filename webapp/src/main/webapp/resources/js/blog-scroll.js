  var ScrollToTop = function (options) {
    this.gjdoc = gj('body');
    this.options = gj.extend(ScrollToTop.defaults, options);

    var namespace = this.options.namespace;

    if (this.options.skin === null) {
      this.options.skin = 'default';
    }

    this.classes = {
      skin: namespace + '_' + this.options.skin,
      trigger: namespace,
      animating: namespace + '_animating',
      show: namespace + '_show'
    };

    this.disabled = false;
    this.useMobile = false;
    this.isShow = false;

    var self = this;
    gj.extend(self, {
      init: function () {
        self.transition = self.transition();
        self.build();


        if (self.options.target) {
          if (typeof self.options.target === 'number') {
            self.target = self.options.target;
          } else if (typeof self.options.target === 'string') {
            self.target = Math.floor(gj(self.options.target).offset().top);
          }
        } else {
          self.target = 0;
        }

        self.gjtrigger.on('click.scrollToTop', function () {
          self.gjdoc.trigger('ScrollToTop::jump');
          return false;
        });

        // bind events
        self.gjdoc.on('ScrollToTop::jump', function () {
          if (self.disabled) {
            return;
          }

          self.checkMobile();

          var speed, easing;

          if (self.useMobile) {
            speed = self.options.mobile.speed;
            easing = self.options.mobile.easing;
          } else {
            speed = self.options.speed;
            easing = self.options.easing;
          }

          self.gjdoc.addClass(self.classes.animating);


          if (self.transition.supported) {
            var pos = gj(window).scrollTop();

            self.gjdoc.css({
              'margin-top': -pos + self.target + 'px'
            });
            gj(window).scrollTop(self.target);

            self.insertRule('.duration_' + speed + '{' + self.transition.prefix + 'transition-duration: ' + speed + 'ms;}');

            self.gjdoc.addClass('easing_' + easing + ' duration_' + speed).css({
              'margin-top': ''
            }).one(self.transition.end, function () {
              self.gjdoc.removeClass(self.classes.animating + ' easing_' + easing + ' duration_' + speed);
            });
          } else {
            gj('html, body').stop(true, false).animate({
              scrollTop: self.target
            }, speed, function () {
              self.gjdoc.removeClass(self.classes.animating);
            });
            return;
          }
        })
            .on('ScrollToTop::show', function () {
              if (self.isShow) {
                return;
              }
              self.isShow = true;

              self.gjtrigger.addClass(self.classes.show);
            })
            .on('ScrollToTop::hide', function () {
              if (!self.isShow) {
                return;
              }
              self.isShow = false;
              self.gjtrigger.removeClass(self.classes.show);
            })
            .on('ScrollToTop::disable', function () {
              self.disabled = true;
              self.gjdoc.trigger('ScrollToTop::hide');
            })
            .on('ScrollToTop::enable', function () {
              self.disabled = false;
              self.toggle();
            });

        gj(window).on('scroll', self._throttle(function () {
          if (self.disabled) {
            return;
          }

          self.toggle();
        }, self.options.throttle));

        if (self.options.mobile) {
          gj(window).on('resize', self._throttle(function () {
            if (self.disabled) {
              return;
            }

            self.checkMobile();
          }, self.options.throttle));
        }

        self.toggle();
      },
      checkMobile: function () {
        var width = gj(window).width();

        if (width < self.options.mobile.width) {
          self.useMobile = true;
        } else {
          self.useMobile = false;
        }
      },
      build: function () {
        if (self.options.trigger) {
          self.gjtrigger = gj(self.options.trigger);
        } else {
          self.gjtrigger = gj('<a href="#" class="' + self.classes.trigger + ' ' + self.classes.skin + '">' + self.options.text + '</a>').appendTo(gj('body'));
        }

        self.insertRule('.' + self.classes.show + '{' + self.transition.prefix + 'animation-duration: ' + self.options.animationSpeed + 'ms;' + self.transition.prefix + 'animation-name: ' + self.options.namespace + '_' + self.options.animation + ';}');

        if (self.options.mobile) {
          self.insertRule('@media (max-width: ' + self.options.mobile.width + 'px){.' + self.classes.show + '{' + self.transition.prefix + 'animation-duration: ' + self.options.mobile.animationSpeed + 'ms !important;' + self.transition.prefix + 'animation-name: ' + self.options.namespace + '_' + self.options.mobile.animation + '  !important;}}');
        }
      },
      can: function () {
        var distance;
        if (self.useMobile) {
          distance = self.options.mobile.distance;
        } else {
          distance = self.options.distance;
        }
        if (gj(window).scrollTop() > distance) {
          return true;
        } else {
          return false;
        }
      },
      toggle: function () {
        if (self.can()) {
          self.gjdoc.trigger('ScrollToTop::show');
        } else {
          self.gjdoc.trigger('ScrollToTop::hide');
        }
      },

      transition: function () {
        var e,
            end,
            prefix = '',
            supported = false,
            el = document.createElement("fakeelement"),
            transitions = {
              "WebkitTransition": "webkitTransitionEnd",
              "MozTransition": "transitionend",
              "OTransition": "oTransitionend",
              "transition": "transitionend"
            };
        for (e in transitions) {
          if (el.style[e] !== undefined) {
            end = transitions[e];
            supported = true;
            break;
          }
        }
        if (/(WebKit)/i.test(window.navigator.userAgent)) {
          prefix = '-webkit-';
        }
        return {
          prefix: prefix,
          end: end,
          supported: supported
        };
      },
      insertRule: function (rule) {
        if (self.rules && self.rules[rule]) {
          return;
        } else if (self.rules === undefined) {
          self.rules = {};
        } else {
          self.rules[rule] = true;
        }

        if (document.styleSheets && document.styleSheets.length) {
          document.styleSheets[0].insertRule(rule, 0);
        } else {
          var style = document.createElement('style');
          style.innerHTML = rule;
          document.head.appendChild(style);
        }
      },
      /**
       * _throttle
       * @description Borrowed from Underscore.js
       */
      _throttle: function (func, wait) {
        var _now = Date.now || function () {
          return new Date().getTime();
        };
        var context, args, result;
        var timeout = null;
        var previous = 0;
        var later = function () {
          previous = _now();
          timeout = null;
          result = func.apply(context, args);
          context = args = null;
        };
        return function () {
          var now = _now();
          var remaining = wait - (now - previous);
          context = this;
          args = arguments;
          if (remaining <= 0) {
            clearTimeout(timeout);
            timeout = null;
            previous = now;
            result = func.apply(context, args);
            context = args = null;
          } else if (!timeout) {
            timeout = setTimeout(later, remaining);
          }
          return result;
        };
      }
    });

    this.init();
  };

  // Default options
  ScrollToTop.defaults = {
    distance: 200,
    speed: 1000,
    easing: 'linear',
    animation: 'fade', // fade, slide, none
    animationSpeed: 500,

    mobile: {
      width: 768,
      distance: 100,
      speed: 1000,
      easing: 'easeInOutElastic',
      animation: 'slide',
      animationSpeed: 200
    },

    trigger: null, // Set a custom triggering element. Can be an HTML string or jQuery object
    target: null, // Set a custom target element for scrolling to. Can be element or number
    text: 'Scroll To Top', // Text for element, can contain HTML

    skin: null,
    throttle: 250,

    namespace: 'scrollToTop'
  };

  ScrollToTop.prototype = {
    constructor: ScrollToTop,
    jump: function () {
      this.gjdoc.trigger('ScrollToTop::jump');
    },
    disable: function () {
      this.gjdoc.trigger('ScrollToTop::disable');
    },
    enable: function () {
      this.gjdoc.trigger('ScrollToTop::enable');
    },
    destroy: function () {
      this.gjtrigger.remove();
      this.gjdoc.data('ScrollToTop', null);
      this.gjdoc.off('ScrollToTop::enable')
          .off('ScrollToTop::disable')
          .off('ScrollToTop::jump')
          .off('ScrollToTop::show')
          .off('ScrollToTop::hide');
    }
  };

  gj.fn.scrollToTop = function (options) {
    if (typeof options === 'string') {
      var method = options;
      var method_arguments = arguments.length > 1 ? Array.prototype.slice.call(arguments, 1) : undefined;

      return this.each(function () {
        var api = gj.data(this, 'scrollToTop');

        if (api && typeof api[method] === 'function') {
          api[method].apply(api, method_arguments);
        }
      });
    } else {
      return this.each(function () {
        var api = gj.data(this, 'scrollToTop');
        if (!api) {
          api = new ScrollToTop(options);
          gj.data(this, 'scrollToTop', api);
        }
      });
    }
  };

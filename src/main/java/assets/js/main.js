;(function () {

	'use strict';

	var isMobile = {
		Android: function() {
			return navigator.userAgent.match(/Android/i);
		},
			BlackBerry: function() {
			return navigator.userAgent.match(/BlackBerry/i);
		},
			iOS: function() {
			return navigator.userAgent.match(/iPhone|iPad|iPod/i);
		},
			Opera: function() {
			return navigator.userAgent.match(/Opera Mini/i);
		},
			Windows: function() {
			return navigator.userAgent.match(/IEMobile/i);
		},
			any: function() {	
			return (isMobile.Android() || isMobile.BlackBerry() || isMobile.iOS() || isMobile.Opera() || isMobile.Windows());
		}
	};

	var mobileMenuOutsideClick = function() {

		$(document).click(function (e) {
	    var container = $("#colorlib-offcanvas, .js-colorlib-nav-toggle");
	    if (!container.is(e.target) && container.has(e.target).length === 0) {

	    	if ( $('body').hasClass('offcanvas') ) {
    			$('body').removeClass('offcanvas');
    			$('.js-colorlib-nav-toggle').removeClass('active');

	    	}
	    }
		});

	};


	var offcanvasMenu = function() {

		if(isMobile.any()){
			$('#page').prepend('<div id="colorlib-offcanvas" />');
			//removes dual burger menu from Cart Overview
			//$('#page').prepend('<a href="#" class="js-colorlib-nav-toggle colorlib-nav-toggle colorlib-nav-white"><i></i></a>');
			
			//clone menu for mobile view
			var clone1 = $('.menu-1 > ul').clone();
			$('#colorlib-offcanvas').append(clone1);
			var clone2 = $('.menu-2 > ul').clone();
			$('#colorlib-offcanvas').append(clone2);
	
	
	
			//shows the hidden user menu and cart menu in mobile view
			//$('#hide1.offcanvas-has-dropdown').style.display = "";
			document.getElementById("hide1").style.display = "block";
			document.getElementById("hide2").style.display = "block";
	
			//Hides the visible user menu and cart menu in mobile view
			//document.getElementById("hide3").style.display = "none";
	
			$('#colorlib-offcanvas .has-dropdown').addClass('offcanvas-has-dropdown');
			$('#colorlib-offcanvas')
				.find('li')
				.removeClass('has-dropdown');
			$(".offcanvas-has-dropdown > a").removeAttr("href");
	
		
			// Hover dropdown menu on mobile
			$('.offcanvas-has-dropdown').mouseenter(function(){
				var $this = $(this);
	
				$this
					.addClass('active')
					.find('ul')
					.slideDown(500, 'easeOutExpo');
			}).mouseleave(function(){
	
				var $this = $(this);
				$this
					.removeClass('active')
					.find('ul')
					.slideUp(500, 'easeOutExpo');
			});
	
	
			$(window).resize(function(){
	
				if ( $('body').hasClass('offcanvas') ) {
	
					$('body').removeClass('offcanvas');
					$('.js-colorlib-nav-toggle').removeClass('active');
	
				}
			});
		}

	};


	var burgerMenu = function() {
		$('body').on('click', '.js-colorlib-nav-toggle', function(event) {
		  var $this = $(this);
	
		  if ($('body').hasClass('offcanvas')) {
			$('body').removeClass('offcanvas');
			$('.js-colorlib-nav-toggle').removeClass('active');
		  } else {
			$('body').addClass('offcanvas');
			$('.js-colorlib-nav-toggle').addClass('active');
		  }
	
		  // Remove the element with the id 'btnCartOverviewForm'
		  $('#btnCartOverviewForm').remove();
		});
	  };
	

	var contentWayPoint = function() {
		var i = 0;
		$('.animate-box').waypoint( function( direction ) {

			if( direction === 'down' && !$(this.element).hasClass('animated-fast') ) {
				
				i++;

				$(this.element).addClass('item-animate');
				setTimeout(function(){

					$('body .animate-box.item-animate').each(function(k){
						var el = $(this);
						setTimeout( function () {
							var effect = el.data('animate-effect');
							if ( effect === 'fadeIn') {
								el.addClass('fadeIn animated-fast');
							} else if ( effect === 'fadeInLeft') {
								el.addClass('fadeInLeft animated-fast');
							} else if ( effect === 'fadeInRight') {
								el.addClass('fadeInRight animated-fast');
							} else {
								el.addClass('fadeInUp animated-fast');
							}

							el.removeClass('item-animate');
						},  k * 200, 'easeInOutExpo' );
					});
					
				}, 100);
				
			}

		} , { offset: '85%' } );
	};


	var dropdown = function() {

		$('.has-dropdown').mouseenter(function(){

			var $this = $(this);
			$this
				.find('.dropdown')
				.css('display', 'block')
				.addClass('animated-fast fadeInUpMenu');

		}).mouseleave(function(){
			var $this = $(this);

			$this
				.find('.dropdown')
				.css('display', 'none')
				.removeClass('animated-fast fadeInUpMenu');
		});

	};


	var goToTop = function() {

		$('.js-gotop').on('click', function(event){
			
			event.preventDefault();

			$('html, body').animate({
				scrollTop: $('html').offset().top
			}, 500, 'easeInOutExpo');
			
			return false;
		});

		$(window).scroll(function(){

			var $win = $(window);
			if ($win.scrollTop() > 200) {
				$('.js-top').addClass('active');
			} else {
				$('.js-top').removeClass('active');
			}

		});
	
	};

	// var increment = function(){

	// };


	// Loading page
	var loaderPage = function() {
		$(".colorlib-loader").fadeOut("slow");
	};


	var sliderMain = function() {
      // Initialize the hero slider
      $('#colorlib-hero .flexslider').flexslider({
        animation: "fade",
        slideshowSpeed: 3000,
        directionNav: true,
        controlNav: true,
        start: function() {
          setTimeout(function() {
            $('.slider-text').removeClass('animated fadeInUp');
            $('.flex-active-slide').find('.slider-text').addClass('animated fadeInUp');
          }, 500);
        },
        before: function() {
          setTimeout(function() {
            $('.slider-text').removeClass('animated fadeInUp');
            $('.flex-active-slide').find('.slider-text').addClass('animated fadeInUp');
          }, 500);
        }
      });

      // Initialize the intro slider
      $('.colorlib-intro .flexslider').flexslider({
        animation: "slide",
        slideshow: false,
        slideshowSpeed: 6000,
        directionNav: false,
        controlNav: false,

      });
    };


	// Owl Carousel
	var owlCrouselFeatureSlide = function() {
		var owl = $('.owl-carousel');
		owl.owlCarousel({
		   animateOut: 'fadeOut',
		   animateIn: 'fadeIn',
		   autoplay: false,
		   autoplayHoverPause: true,
		   loop:true,
		   margin:0,
		   nav:false,
		   dots: true,
		   autoHeight: false,
		   items: 1,
		   navText: [
		      "<i class='icon-chevron-left owl-direction'></i>",
		      "<i class='icon-chevron-right owl-direction'></i>"
	     	]
		});

		var owl2 = $('.owl-carousel2');
		owl2.owlCarousel({
			animateOut: 'fadeOut',
		   animateIn: 'fadeIn',
		   autoplay: true,
		   autoplayHoverPause: true,
		   loop:true,
		   margin:0,
		   nav:false,
		   dots: false,
		   autoHeight: true,
		   items: 1,
		   navText: [
		      "<i class='icon-chevron-left owl-direction'></i>",
		      "<i class='icon-chevron-right owl-direction'></i>"
	     	]
		});
	};

	var parallax = function() {

		if ( !isMobile.any() ) {
			$(window).stellar({
				horizontalScrolling: false,
				hideDistantElements: false, 
				responsive: true

			});
		}
	};

	var datePicker = function() {
		// jQuery('#time').timepicker();
		jQuery('.date').datepicker({
		  'format': 'm/d/yyyy',
		  'autoclose': true
		});
	};

   // checks if the type of device is a mobile device
   var deviceCheck = function() {
      return (typeof window.orientation !== "undefined") || (navigator.userAgent.indexOf('IEMobile') !== -1);
    }
    //If true, modifies the process chain for the mobile view.
    if (deviceCheck()) {
	  $('#deleteProductModal .modal-content').css('width', '50%');
	  $('.btn.btn-account').css('width', '40%');


      var processAfterStyle = document.createElement('style');
        processAfterStyle.innerHTML = ".process:after { height: 0px; }";
        document.head.appendChild(processAfterStyle);
      var processElements = document.getElementsByClassName("process");
      for (var i = 0; i < processElements.length; i++) {
        processElements[i].style.width = "33.333%";
      }


    } else {
    }

	$(function(){

        deviceCheck();
		mobileMenuOutsideClick();
		offcanvasMenu();
		burgerMenu();
		contentWayPoint();
		sliderMain();
		dropdown();
		goToTop();
		loaderPage();
		owlCrouselFeatureSlide();
		parallax();
		datePicker();

	});

}());
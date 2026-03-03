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

	};


	var offcanvasMenu = function() {

		if(isMobile.any()){
			
			//removes dual burger menu from Cart Overview
			//$('#page').prepend('<a href="#" class="js-colorlib-nav-toggle colorlib-nav-toggle colorlib-nav-white"><i></i></a>');
			
			//clone menu for mobile view
	
	
			//shows the hidden user menu and cart menu in mobile view
			//$('#hide1.offcanvas-has-dropdown').style.display = "";
			
	
			//Hides the visible user menu and cart menu in mobile view
			//document.getElementById("hide3").style.display = "none";
	
		
			// Hover dropdown menu on mobile
	

		}

	};


	var burgerMenu = function() {

	};


	function goToTop() {

		let toTopContainer = document.getElementById('js-top');

		window.onscroll = function() {scrollFunction()};

		function scrollFunction() {
			if (document.body.scrollTop > 200 || document.documentElement.scrollTop > 200) {
				toTopContainer.style.display = "block";
			} else {
				toTopContainer.style.display = "none";
			}
		}
	
	};


   // checks if the type of device is a mobile device
   var deviceCheck = function() {
      return (typeof window.orientation !== "undefined") || (navigator.userAgent.indexOf('IEMobile') !== -1);
    }
    //If true, modifies the process chain for the mobile view.
	/*
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
	*/

	function siteJS(){

        deviceCheck();
		mobileMenuOutsideClick();
		offcanvasMenu();
		burgerMenu();
		goToTop();

	};

	siteJS();

}());

function toTopFunction(){
	document.body.scrollTop = 0; //Safari
	document.documentElement.scrollTop = 0; //Other major browsers
};
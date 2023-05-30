function showCartSlider(show) {

	getMiniCartText();

}

function showMiniCart() {
/*	var url = CONTEXT_PATH + '/getMiniCartElements';
	$.get(url, getMiniCartText); */
	getMiniCartText();
}

function getMiniCartText() {

	$(".notificationsLoader").html(
			'<img src="' + CONTEXT_PATH + '/assets/img/loader.gif">');
	$.ajax({
		type : "GET",
		url : CONTEXT_PATH + '/getMiniCartElements',
		success : function(data) {
			$('.cartMiniElementList').empty();

			/* start with the last product*/
			for (var i = (data.productsInCartList.length-1); i >= 0; i--) {

				// create new <li> element
				var inner = getMiniCartElementInnerHtml(
						data.productsInCartList[i], data.currency,
						data.unitLength);
				var liElement = $(
						"<li class='cartMiniProductsListItems'></li>")
						.html(inner);

				$(".cartMiniElementList").prepend(liElement);

			}

			// update product counter in mini-cart
			$(".cartMiniProductCounter span.value").text(
					data.cartProductCount);
			// update total sub Order Price
			$('.subOrderPrice').text(
					data.currency + data.subTotalPrice);

			$(".notificationsLoader").empty();
		}
	});

}

/* add product to cart */
function addToCart(productId, finish, size) {
	addToMiniCart(productId, finish, size);
}
function addToMiniCart(productId, finish, size) {
	$(".notificationsLoader").html(
			'<img src="' + CONTEXT_PATH + '/assets/img/loader.gif">');
	$.ajax({
		type : "GET",
		url : CONTEXT_PATH + '/addToCartSlider' + '?productId=' + productId
				+ '&finish=' + finish + '&size=' + size,
		success : function(data) {
			// create new <li> element
			var liId = "productId" + data.product.productId + data.product.finish + data.product.size.width + "x" + data.product.size.height;
			var inner = getMiniCartElementInnerHtml(data.product, data.currency, data.unitLength);
			var liElement = $("<li class='" + liId +  " miniCartItem'" + "></li>").html(inner);
			
			/* remove existing product*/
			$("." + liId).remove();				
			/* replace */
			$(".cartMiniElementList").prepend(liElement);
			/* update total sub Order Price */
			$('.subOrderPrice').text(
					data.currency + data.subOrderTotal);
			/* notification off */
			$(".notificationsLoader").empty();

			/* update cart in header (counter) */
			$("#headerCartOverview span.headerCartProductCount").text(
					data.headerCartOverview);
			/* update product counter in mini-cart */
			$(".cartMiniProductCounter span.value").text(
					data.headerCartOverview);

			/* show minicart and than hide after 1500 ms */
			$('.top-menu #headerCartOverview').attr('aria-expanded', 'true').parent().addClass('show');
            $('.top-menu #miniCartMenu').addClass('show');
			window.setTimeout(function(){
			 $('.top-menu #headerCartOverview').attr('aria-expanded', 'false').parent().removeClass('show');
			  $('.top-menu #miniCartMenu').removeClass('show');
            }, 1500);


		}
	});
}

/* return a (html)list with all products from the cart*/
function getMiniCartElementInnerHtml(product, currency, unitLength) {
	
	return '<ul class="cartItems list-unstyled">' + '<li class="prodName">'
	+ product.productName + '</li>'
	+ '<li>Quantity: <span class="prodCount">' + product.productCount
	+ '</span>' + ' (<span class="prodStyle">' + product.finish
	+ '</span>, <span class="prodSize">' + '<span class ="prodWidth">' + product.size.width + '</span>'  
	+ " x " + '<span class ="prodHeight">' + product.size.height + '</span>' + " " + '<span class = "unitLength">' + unitLength + '</span>'
	+ " " + '</span>)' + '</li>'
	+ '<li>' + '<div class="prodPrice text-right"><strong>' + currency
	+ product.productTotalUnitPrice + '</strong></div>' + '</li>' + '<ul>';
}
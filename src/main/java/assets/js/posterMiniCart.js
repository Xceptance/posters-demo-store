function showCartSlider(show) {

	getMiniCartText();

}

function showMiniCart() {
	var url = CONTEXT_PATH + '/getCartElementSlider';
	$.get(url, getMiniCartText);
}

function getMiniCartText() {

	$("#notificationsLoader").html(
			'<img src="' + CONTEXT_PATH + '/assets/img/loader.gif">');
	$('#cartMiniElementList').empty();
	$
			.ajax({
				type : "GET",
				url : CONTEXT_PATH + '/getCartElementSlider',
				success : function(data) {

					for (var i = 0; i < data.productsInCartList.length; i++) {

						// create new <li> element
						var inner = getCartSliderElementInnerHtml(
								data.productsInCartList[i], data.currency,
								data.unitLength);
						var liElement = $(
								"<li class='cartMiniProductsListItems'></li>")
								.html(inner);

						$("#cartMiniProductsList").prepend(liElement);

					}

					// update total sub Order Price
					$('#cartMiniTotalSubOrderPrice').text(
							data.currency + data.subOrderTotal);
					$("#notificationsLoader").empty();

					// update cart in header (counter)
					$("#headerCartOverview span.headerCartProductCount").text(
							data.headerCartOverview);
					// update product counter in mini-cart
					$("#cartMiniWrap span.cartMiniCartProductCounter").text(
							data.headerCartOverview);
				}
			});

}

function updateCartSlider(data) {
	// clear list
	$('#cartMiniElementList').empty();
	// add products to list
	for (var i = 0; i < data.cartElements.length; i++) {

		var inner = getCartSliderElementInnerHtml(data.cartElements[i],
				data.currency, data.unitLength);
		var liElement = $("<li class='cartMiniElementListItems'></li>").html(
				inner);
		$("#cartMiniElementList").append(liElement);
	}

	// update total price in mini-cart
	$('#cartMiniTotalSubOrderPrice').text(data.currency + data.subOrderTotal);
	// $('#cartProductCount').text(data.currency + data.subOrderTotal);
}

function addToCart(productId, finish, size) {
	/* add product to cart */
	addToMiniCart(productId, finish, size);
}

function getMiniCartElementInnerHtml(product, currency, unitLength) {

	return '<ul class="cartItems list-unstyled">' + '<li class="prodName">'
			+ product.productName + '</li>'
			+ '<li>Quantity: <span class="prodCount">' + product.productCount
			+ '</span>' + ' (<span class="prodStyle">' + product.finish
			+ '</span>, <span class="prodSize">' + product.size.width + " x "
			+ product.size.height + " " + unitLength + '</span>)' + '</li>'
			+ '<li>' + '<div class="prodPrice text-right"><strong>' + currency
			+ product.productPrice + '</strong></div>' + '</li>' + '<ul>';
}

function addToMiniCart(productId, finish, size) {
	$("#notificationsLoader").html(
			'<img src="' + CONTEXT_PATH + '/assets/img/loader.gif">');
	/* $('#cartMiniElementList').empty(); */
	$.ajax({
		type : "GET",
		url : CONTEXT_PATH + '/addToCartSlider' + '?productId=' + productId
				+ '&finish=' + finish + '&size=' + size,
		success : function(data) {
			// create new <li> element
			var liId = "productId" + data.product.productId + data.product.finish + data.product.size.width + "x" + data.product.size.height;
			var inner = getMiniCartElementInnerHtml(data.product, data.currency, data.unitLength);
			var liElement = $("<li id='" + liId + "' class=\"miniCartItem\"></li>").html(inner);
			
			/* exist product then replace, if not exist append product*/
			if( $("#" + liId).length > 0)
			{
				$("#" + liId).remove();
				$("#cartMiniElementList").prepend(liElement);
			}
			else
			{
				$("#cartMiniElementList").prepend(liElement);
			}
			
			/* replace */
			$("#cartMiniElementList").prepend(liElement);

			/* update total sub Order Price */
			$('#cartMiniTotalSubOrderPrice').text(
					data.currency + data.subOrderTotal);
			/* notification off */
			$("#notificationsLoader").empty();

			/* update cart in header (counter) */
			$("#headerCartOverview span.headerCartProductCount").text(
					data.headerCartOverview);
			/* update product counter in mini-cart */
			$("#cartMiniWrap span.cartMiniCartProductCounter").text(
					data.headerCartOverview);

			$('#headerCartOverview').dropdown("toggle");
			window.setTimeout(function(){
				$('#headerCartOverview').dropdown("toggle");
			},1500);
		}
	});
}
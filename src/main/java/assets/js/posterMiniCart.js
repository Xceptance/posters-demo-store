function showCartSlider(show) {

	getCartSliderText();

}

function getCartSliderText() {
	var url = CONTEXT_PATH + '/getCartElementSlider';
	$.get(url, updateCartSlider);
}

function updateCartSlider(data) {
	// clear list
	$('#cartMiniElementList').empty();
	// add products to list
	for (var i = 0; i < data.cartElements.length; i++) {

		var inner = setCartSliderElementInnerHtml(data.cartElements[i],
				data.currency, data.unitLength);
		var liElement = $("<li class='cartSliderElementListItems'></li>").html(
				inner);
		$("#cartMiniElementList").append(liElement);
	}

	// update total price in cart slider
	$('#cartMiniTotalSubOrderPrice').text(data.currency + data.subOrderTotal);
	// $('#cartProductCount').text(data.currency + data.subOrderTotal);
}

function setCartSliderElementInnerHtml(product, currency, unitLength) {

	return '<ul class="cartItems list-unstyled">' + '<li class="prodName">'
			+ product.productName + '</li>'
			+ '<li>Quantity: <span class="prodCount">' + product.productCount
			+ '</span>' + ' (<span class="prodStyle">' + product.finish
			+ '</span>, <span class="prodSize">' + product.size.width + " x "
			+ product.size.height + " " + unitLength + '</span>)' + '</li>'
			+ '<li>' + '<div class="prodPrice text-right"><strong>' + currency
			+ product.productPrice + '</strong></div>' + '</li>' + '<ul>';
}

function addToCart(productId, finish, size) {
	getCartSliderText();
	addToCartSlider(productId, finish, size);
}

function addToCartSlider(productId, finish, size) {
	$("#notificationsLoader").html(
			'<img src="' + CONTEXT_PATH + '/assets/img/loader.gif">');

	$.ajax({
		type : "GET",
		url : CONTEXT_PATH + '/addToCartSlider' + '?productId=' + productId
				+ '&finish=' + finish + '&size=' + size,
		success : function(data) {
			// create new <li> element
			var liId = "productId" + data.product.productId
					+ data.product.finish + data.product.size.width + "x"
					+ data.product.size.height;
			var inner = setCartSliderElementInnerHtml(data.product,
					data.currency, data.unitLength);
			var liElement = $("<li id='" + liId + "'></li>").html(inner);
			if ($("#" + liId).length > 0) {
				$("#" + liId).remove();
				$("#cartSliderElementList").prepend(liElement);
			} else {
				$("#cartSliderElementList").prepend(liElement);
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
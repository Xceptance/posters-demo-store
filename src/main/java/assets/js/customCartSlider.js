function showCartSlider(show) {
	if (show === true) {
		$("#slidingTopWrap").clearQueue().attr('style', 'display:none;')
				.slideDown("slow", function() {
					if ($("#slidingTopWrap").is(":visible")) {
						getCartSliderText();
					}
				});
	} else if (show === false) {
		$("#slidingTopWrap").clearQueue().slideUp("slow").style;
	} else {
		$("#slidingTopWrap").clearQueue().slideToggle("slow", function() {
			if ($("#slidingTopWrap").is(":visible")) {
				getCartSliderText();
			}
		});
	}
}

function getCartSliderText() {
	var url = CONTEXT_PATH + '/getCartElementSlider';
	$.get(url, updateCartSlider);
}

function updateCartSlider(data) {
	// clear list
	$('#cartSliderElementList').empty();
	// add products to list
	for (var i = 0; i < data.cartElements.length; i++) {
		var liId = "productId" + data.cartElements[i].productId
				+ data.cartElements[i].finish + data.cartElements[i].size.width
				+ "x" + data.cartElements[i].size.height;
		var inner = setCartSliderElementInnerHtml(data.cartElements[i],
				data.currency, data.unitLength);
		var liElement = $("<li id='" + liId + "'></li>").html(inner);
		$("#cartSliderElementList").append(liElement);
	}
	// update total price in cart slider
	$('#cartSliderSubOrderPrice').text(data.currency + data.subOrderTotal);
}

function setCartSliderElementInnerHtml(product, currency, unitLength) {

	return '<ul class="list-unstyled">' + '<li class="prodName">'
			+ product.productName + '</li>'
			+ '<li>Quantity: <span class="prodCount">' + product.productCount + '</span>'
			+ ' (<span class="prodStyle">' + product.finish
			+ '</span>, <span class="prodSize">' + product.size.width + " x "
			+ product.size.height + " " + unitLength + '</span>)' + '</li>'
			+ '<li>' + '<div class="prodPrice text-right"><strong>' + currency
			+ product.productPrice + '</strong></div>' + '</li>' + '<ul>';

}

function addToCart(productId, finish, size) {
	getCartSliderText();
	if ($("#slidingTopWrap").is(":visible")) {
		addToCartSlider(productId, finish, size);
	} else {
		$("#slidingTopWrap").clearQueue().attr('style', 'display:none;')
				.slideDown("slow", function() {
					addToCartSlider(productId, finish, size);
				}).delay(2000).slideUp("slow");
	}
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
			// update total price in cart slider
			$('#cartSliderSubOrderPrice').text(
					data.currency + data.subOrderTotal);
			$("#notificationsLoader").empty();
			// update cart in header
			$("#headerCartOverview span.CartOverview").text(
					data.headerCartOverview);
		}
	});
}

$(document).ready(function() {
	$("#cartSliderItemsWrap li:first").hide();
	$("#slidingTopWrap").hide();

	$('#cartSliderItemsWrap').mouseover(function() {
		showCartSlider(true);
	});
	$('#btnCartOverview').mouseenter(function() {
		showCartSlider(true);
	});
	$('#btnCartOverview').mouseleave(function() {
		showCartSlider(false);
	});
});

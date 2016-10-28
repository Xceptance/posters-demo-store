$(document).ready(function() {
	$('#header-search-trigger').click(function() {
		$('#header-menu-search').css('display', 'block');
		$('#header-search-trigger').css('display', 'none');
	});
	$('#btnSearch').click(function() {
		$('#header-menu-search').css('display', 'none');
		$('#header-search-trigger').css('display', 'block');
	});
});

function deleteFromCart(cartProductId, cartIndex) {
	updateProductCount(cartProductId, 0, cartIndex);
}

function updateProductCount(cartProductId, count, cartIndex) {
	var url = CONTEXT_PATH + '/updateProductCount?cartProductId=' + cartProductId
			+ '&productCount=' + count;
	$.get(url).always(function() {
	}).done(function(data) {
		// update cart in header
		$("#headerCartOverview .headerCartProductCount").text(data.headerCartOverview);

		// update total unit price
		$('#product' + cartIndex +" .productTotalUnitPrice").text(data.currency + data.totalUnitPrice);

		//subtotal
		$("#orderSubTotalValue").text(data.currency + data.subTotalPrice);
		//Tax
		$("#orderSubTotalTaxValue").text(data.currency + data.totalTaxPrice);
		// update total price
		$("#orderTotal").text(data.currency + data.totalPrice);
		
		// update mini cart
		getMiniCartText();
		// update cart overview, if count was zero
		if (count == 0) {
			// remove product from cart overview
			$('#product' + cartIndex).remove();
			//reload page to show empty cart if no products left
			if(!$('.cartOverviewProduct').length){
				window.location.reload();
			}							
		}
	}).error(function(data) {
		var errMsg = eval("(" + data.responseText + ")");
		$("#errorMessage").show();
		$("#errorMessage div strong").text(errMsg.message);
	});
}

// update price of product if the selected size has changed
function updatePrice(selectedField, productId) {
	var url = CONTEXT_PATH + '/updatePrice';
	$.post(url, {
		size : selectedField.value,
		productId : productId
	}, function(data) {
		$('#prodPrice').text(data.newPrice);
	});
}

function updateProductOverview(data) {
	var i = 0;
	// show all products
	while ($('#product' + i).length) {
		$('#product' + i).show();
		i++;
	}
	// set new content of products
	for (i = 0; i < data.products.length; i++) {
		$('#product' + i + " .pName").text(data.products[i].name);
		$('#product' + i + " a").attr(
				"href",
				CONTEXT_PATH + "/productDetail/" + encodeURIComponent(data.products[i].name) + "?productId="
						+ data.products[i].id);
		$('#product' + i + " a img").attr("src", CONTEXT_PATH + data.products[i].imageURL);
		$('#product' + i + " .pDescriptionOverview").text(
				data.products[i].descriptionOverview);
		$('#product' + i + " .pPrice").text(data.products[i].priceAsString);
	}
	// hide not updated products
	while ($('#product' + i).length) {
		$('#product' + i).hide();
		i++;
	}
	// set current page attribute
	$('#productOverview').attr("currentPage",data.currentPage);
}

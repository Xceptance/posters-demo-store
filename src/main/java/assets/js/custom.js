function deleteFromCart(basketProductId, cartIndex)
{
	var url = '/deleteFromCart?basketProductId=' + basketProductId;
	$.get(url, function(data)
		{
			// remove product from cart overview
			$('#product' + cartIndex).remove();
			// update cart in header
			$("#headerBasketOverview span").text(data.headerCartOverview);
			// update total price
			$("#totalPrice:first-child").text(data.totalPrice);
			// update cart slider
			getCartSliderText();
		});
}

function updateProductCount(basketProductId, count, cartIndex, event)
{
	var url = '/updateProductCount?basketProductId=' + basketProductId + '&productCount=' + count;
	$.get(url, function(data)
		{
			if (data.error == false)
			{
				// update cart in header
				$("#headerBasketOverview span").text(data.headerCartOverview);
				// update total price
				$("#totalPrice:first-child").text(data.totalPrice);
				// update cart slider
				getCartSliderText();
			}
			else
			{
				alert(data.message);
			}
		});
}
function showCartSlider(show) {

	getMiniCartText();

}

function showMiniCart() {
	getMiniCartText();
}

function getMiniCartText() {

	document.querySelector(".notifications-loader").innerHTML = ['<img src="' + CONTEXT_PATH + '/assets/img/loader.gif">'];
	var url = CONTEXT_PATH + '/getMiniCartElements';
	const xhr = new XMLHttpRequest();

	xhr.onreadystatechange = function(data) {
		if(xhr.readyState !== 4) return;

		if(xhr.status === 200) {
			var minicartElementList = document.querySelector('.cart-mini-element-list')
			minicartElementList.innerHTML = [''];

			for (var i = (data.productsInCartList.length-1); i >= 0; i--) {
				var minicartElement = document.createElement("li");
				minicartElement.classList.add('cart-mini-products-list-items');
				minicartElement.innerHTML = getMiniCartElementInnerHtml(
					data.productsInCartList[i], data.currency,
					data.unitLength);
				minicartElementList.append(minicartElement);
			}

			document.querySelector(".cart-mini-product-counter span.value").textContent = data.cartProductCount;
			document.querySelector('.suborder-price').textContent = data.currency + data.subTotalPrice;

			document.querySelector(".notifications-loader").innerHTML = [''];
		} else {
			sendAlert("failed to update mini cart", "alert-danger");
		}
	}
	xhr.open("GET", url);
	xhr.send();
}

/* add product to cart */
function addToCart(productId, finish, size) {
	addToMiniCart(productId, finish, size);
}
function addToMiniCart(productId, finish, size) {
	document.getElementById('mini-cart-menu').style.display = "block";
	document.querySelector(".notifications-loader").innerHTML = ['<img src="' + CONTEXT_PATH + '/assets/img/loader.gif">'];
	var liId = "productId" + productId + finish + size.width + "x" + size.height;
	var url = CONTEXT_PATH + '/addToCartSlider' + '?productId=' + productId + '&finish=' + finish + '&size=' + size;
	const xhr = new XMLHttpRequest();

	xhr.onreadystatechange = function() {
		if(xhr.readyState !== 4) return;

		if(xhr.status === 200) {
			/* remove existing product*/
			try{
				document.querySelector("." + liId).remove();
			}
			catch (error) {

			}
			var data = JSON.parse(xhr.responseText);
			/* replace */
			minicartEntry = document.createElement("li");
			minicartEntry.classList.add(liId);
			minicartEntry.classList.add('mini-cart-item');
			minicartEntry.innerHTML = getMiniCartElementInnerHtml(data.product, data.currency, data.unitLength);
			/* update total sub Order Price */
			document.querySelector('.suborder-price').textContent = data.currency + data.subOrderTotal;
			/* notification off */
			document.querySelector(".notifications-loader").innerHTML = [''];

			/* update cart in header (counter) */
			document.querySelector("#header-cart-overview span.header-cart-product-count").textContent = data.headerCartOverview;
			/* update product counter in mini-cart */
			document.querySelector(".cart-mini-product-counter span.value").textContent = data.headerCartOverview;
			/* display the new product in the minicart */
			document.querySelector(".cart-mini-element-list").append(minicartEntry);
			/* show minicart and than hide after 1500 ms */
			document.querySelector(".top-menu #header-cart-overview").classList.add('show');
			document.querySelector(".top-menu #mini-cart-menu").classList.add('show');
			window.setTimeout(function(){
				document.querySelector(".top-menu #header-cart-overview").classList.remove('show');
				document.querySelector(".top-menu #mini-cart-menu").classList.remove('show');
			}, 1500);
		} else {
			sendAlert("failed to update mini cart", "alert-danger");
		}
	}
	xhr.open("GET", url);
	xhr.send();
}

/* return a (html)list with all products from the cart*/
function getMiniCartElementInnerHtml(product, currency, unitLength) {
	
	return '<ul class="cart-items list-unstyled">' + '<li class="prod-name">'
	+ product.productName + '</li>'
	+ '<li>Quantity: <span class="prod-count">' + product.productCount
	+ '</span>' + ' (<span class="prod-style">' + product.finish
	+ '</span>, <span class="prod-size">' + '<span class ="prod-width">' + product.size.width + '</span>'  
	+ " x " + '<span class ="prod-height">' + product.size.height + '</span>' + " " + '<span class = "unit-length">' + unitLength + '</span>'
	+ " " + '</span>)' + '</li>'
	+ '<li>' + '<div class="prod-price text-right"><strong>' + currency
	+ product.productTotalUnitPrice + '</strong></div>' + '</li>' + '<ul>';
}
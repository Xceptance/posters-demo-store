function addToCart(prodId, finish) {
	var base = '/addToBasket';
	var url = base + '?productId=' + prodId + '&finish=' + finish;
	$.get(url,function(data) {
		document.getElementById("headerBasketOverview").firstChild.nextSibling.nodeValue = data.headerCartOverview;
	});
}
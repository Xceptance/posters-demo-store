// prepare cart slider
window.onload = getCartSliderText;

$(document).ready(function(){ 
	$("#basketItemsWrap li:first").hide();
	$("#slidingTopContent").hide();
	$("#slidingTopTriggerHide").hide();
});

function showCartSlider()
{
      	$("#slidingTopContent").slideToggle("slow", function(){
			if ($("#slidingTopContent").is(":visible")) {
				$("#slidingTopTriggerShow").hide();
				$("#slidingTopTriggerHide").show();
			} else {
				$("#slidingTopTriggerShow").show();
				$("#slidingTopTriggerHide").hide();
			}
		});	
}

function getCartSliderText()
{
	var url = '/getCartElementSlider';
	$.get(url, updateCartSlider);
}

function updateCartSlider(data)
{
	for(var i=0;i<data.cartElements.length;i++)
	{
		var liElement = document.createElement("li");
		liElement.setAttribute("id", "productId" + data.cartElements[i].productId + data.cartElements[i].finish);
		liElement.innerHTML = setCartSliderElementInnerHtml(data.cartElements[i]);
		document.getElementById("cartElementList").appendChild(liElement);
	}
}

function setCartSliderElementInnerHtml(product)
{
	return product.productName + " - " + product.finish + " - " + product.productPrice + " (" + product.productCount + " items)";
}

function addToCart(productId, finish)
{
	if ($("#slidingTopContent").is(":visible"))
	{
		addToCartSlider(productId, finish);
	}
	else
	{
		$("#slidingTopContent").slideToggle("slow", function(){		
			$("#slidingTopTriggerShow").hide();
			$("#slidingTopTriggerHide").show();
			addToCartSlider(productId, finish);
			$("#slidingTopTriggerHide").fadeTo(4000, 1, function(){
				$("#slidingTopContent").slideToggle("slow", function(){
					$("#slidingTopTriggerShow").show();
					$("#slidingTopTriggerHide").hide();
				});
			});
		});
	}
}

function addToCartSlider(productId, finish)
{
	$("#notificationsLoader").html('<img src="/assets/img/loader.gif">');
	
	$.ajax({  
		type: "GET",  
		url: '/addToCartSlider' + '?productId=' + productId + '&finish=' + finish, 
		success: function(data) {
			// create new <li> element
			var liElement = document.createElement("li");
			var liId = "productId" + data.product.productId + data.product.finish;
			liElement.setAttribute("id", liId);
			liElement.innerHTML = setCartSliderElementInnerHtml(data.product);
			if( $("#" + liId).length > 0)
			{
				$("#" + liId).before(liElement).remove();
			}
			else
			{
				var list = document.getElementById("cartElementList");
				list.insertBefore(liElement, list.firstChild);
			}
			$("#notificationsLoader").empty();
			// update cart in header
			document.getElementById("headerBasketOverview").firstChild.nextSibling.nodeValue = data.headerCartOverview;
		}
	});
}

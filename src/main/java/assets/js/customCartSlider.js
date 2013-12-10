$(document).ready(function(){ 
	$("#basketItemsWrap li:first").hide();
	$("#slidingTopContent").hide();
	$("#slidingTopTriggerHide").hide();
});

function showCartSlider()
{
    $("#slidingTopContent").slideToggle("slow", function(){
    	if ($("#slidingTopContent").is(":visible"))
    	{
    		getCartSliderText();
    		$("#slidingTopTriggerShow").hide();
    		$("#slidingTopTriggerHide").show();
    	}
    	else
    	{
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
	// clear list
	$('#cartElementList').empty();
	// add products to list
	for (var i=0; i<data.cartElements.length; i++)
	{
		var liId = "productId" + data.cartElements[i].productId + data.cartElements[i].finish;
		var liElement = "<li id='" + liId + "'>" + setCartSliderElementInnerHtml(data.cartElements[i], data.currency) + "</li>";
		$("#cartElementList").append(liElement);
	}
}

function setCartSliderElementInnerHtml(product, currency)
{
	return "<span id='sliderProdName'>" + product.productName + "</span> - <span id='sliderProdFinish'>" + product.finish + "</span> - <span id='sliderProdPrice'>" + product.productPrice + currency + "</span> (<span id='sliderProdCount'>" + product.productCount + "</span> items)";
}

function addToCart(productId, finish)
{
	getCartSliderText();
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
			var liId = "productId" + data.product.productId + data.product.finish;
			var liElement = "<li id='" + liId + "'>" + setCartSliderElementInnerHtml(data.product, data.currency) + "</li>";
			if( $("#" + liId).length > 0)
			{
				$("#" + liId).remove();
				$("#cartElementList").prepend(liElement);
			}
			else
			{
				$("#cartElementList").prepend(liElement);
			}
			$("#notificationsLoader").empty();
			// update cart in header
			$("#headerBasketOverview span").text(data.headerCartOverview);
		}
	});
}

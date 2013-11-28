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
			liElement.setAttribute("id", "productId" + data.cartElements[i].productId);
			liElement.innerHTML = data.cartElements[i].productName + " " + data.cartElements[i].productPrice + " " + data.cartElements[i].productCount;
			document.getElementById("cartElementList").appendChild(liElement);
		}
}

function addToCartSlider(productId, finish)
{
		if ($("#slidingTopContent").is(":visible")) {

			$("#notificationsLoader").html('<img src="/assets/img/loader.gif">');
		
			$.ajax({  
			type: "GET",  
			url: '/addToCartSlider' + '?productId=' + productId + '&finish=' + finish,
			success: function(data) {
				// create new <li/> element
				var liElement = document.createElement("li");
				liElement.setAttribute("id", "productId" + data.product.productId);
				liElement.innerHTML = data.product.productName + " " + data.product.productPrice + " " + data.product.productCount;
				if( $("#productId" + productId).length > 0){
					$("#productId" + productId).animate({ opacity: 0 }, 500);
					$("#productId" + productId).before(liElement).remove();
					$("#productId" + productId).animate({ opacity: 0 }, 500);
					$("#productId" + productId).animate({ opacity: 1 }, 500);
					$("#notificationsLoader").empty();
					
				} else {
					var list = document.getElementById("cartElementList");
					list.insertBefore(liElement, list.firstChild);
					$("#basketItemsWrap li:first").hide();
					$("#basketItemsWrap li:first").show("slow");  
					$("#notificationsLoader").empty();			
				}
				// update cart in header
				document.getElementById("headerBasketOverview").firstChild.nextSibling.nodeValue = data.headerCartOverview;
			}  
			}); 

		} else {
			$("#slidingTopContent").slideToggle("slow", function(){		
				$("#slidingTopTriggerShow").hide();
				$("#slidingTopTriggerHide").show();
				$("#notificationsLoader").html('<img src="/assets/img/loader.gif">');
			
				$.ajax({  
				type: "GET",  
				url: '/addToCartSlider' + '?productId=' + productId + '&finish=' + finish, 
				success: function(data) {
					// get button text
					textShowCart = data.textShowCart;
					textHideCart = data.textHideCart;
					// create new <li/> element
					var liElement = document.createElement("li");
					liElement.setAttribute("id", "productId" + data.product.productId);
					liElement.innerHTML = data.product.productName + " " + data.product.productPrice + " " + data.product.productCount;
					if( $("#productId" + productId).length > 0){
						$("#productId" + productId).animate({ opacity: 0 }, 500);
						$("#productId" + productId).before(liElement).remove();
						$("#productId" + productId).animate({ opacity: 0 }, 500);
						$("#productId" + productId).animate({ opacity: 1 }, 500);
						$("#notificationsLoader").empty();
						
					} else {
						var list = document.getElementById("cartElementList");
						list.insertBefore(liElement, list.firstChild);
						$("#basketItemsWrap li:first").hide();
						$("#basketItemsWrap li:first").show("slow");  
						$("#notificationsLoader").empty();			
					}
					// update cart in header
					document.getElementById("headerBasketOverview").firstChild.nextSibling.nodeValue = data.headerCartOverview;
				}  
				}); 

				$("#slidingTopTrigger").fadeTo(4000, 1, function(){
					$("#slidingTopContent").slideToggle("slow", function(){
						$("#slidingTopTriggerShow").show();
						$("#slidingTopTriggerHide").hide();
					});
				});
			});
		}
}
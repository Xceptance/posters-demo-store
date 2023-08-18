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
		$('#product' + i + " a img").attr("src", CONTEXT_PATH + data.products[i].imageURL).attr("title", data.products[i].name);
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

//Setup on DOM ready
$(document).ready(function() {
 if ($('body').hasClass('process')) {
        $('.process').style.width = "33%";
        }
	$('#header-search-trigger').click(function() {
		$('#header-menu-search').css('display', 'block');
		$('#header-search-trigger').css('display', 'none');
	});
	$('#btnSearch').click(function() {
		$('#header-menu-search').css('display', 'none');
		$('#header-search-trigger').css('display', 'block');
	});

	//Sync search input of mobile and not mobile
	$("#s").on("keyup paste", function() {
    	$("#sMobile").val($(this).val());
	});
	$("#sMobile").on("keyup paste", function() {
    	$("#s").val($(this).val());
	});

	//Check if searchterm is Empty
	$('#searchForm').submit(function(e){
		var input = $('#s').val().trim();
		if (!input){
			$('#s').val(input);
			return false;
		}
	})

	//Setup click handler for update and delete button
	if ($('#deleteProductModal').length){
		$('.btnUpdateProduct').click(function(){
			var cartProductId = $(this).data('id');
			var cartIndex = $(this).data('index');
			var inputValue = $('#productCount' + cartIndex).val();
			if (inputValue == 0){
				$(this).siblings('.btnRemoveProduct').click();
			}
			else{
				$('#productCount' + cartIndex).prop("defaultValue", inputValue);
				updateProductCount(cartProductId, inputValue, cartIndex);
			}
		});

		//Setup handler for buttons of Confimation popup
		$('#deleteProductModal').on('show.bs.modal', function(e){
			var cartProductId = $(e.relatedTarget).data('id');
			var cartIndex = $(e.relatedTarget).data('index');
			var prodInfo = $(e.relatedTarget).closest('tr.cartOverviewProduct').find('.productInfo').clone();
			var oldValue = $('#productCount' + $(e.relatedTarget).data('index')).prop("defaultValue");
			//Rollback the input value if close button clicked
			$('#buttonClose').click(function(){
				$('#productCount' + cartIndex).val(oldValue);
			});
			//Delete button
			$(this).find('.btn-danger').click(function(){
				deleteFromCart(cartProductId, cartIndex);
			})
			//Bodycontent of popup
			$(this).find('.modal-body').html(prodInfo);			
		});
	}	
});
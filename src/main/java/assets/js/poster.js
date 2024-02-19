const sendAlert = (messageContent, messageType) => {
	const alertPlaceholder = document.getElementById('alertPlaceholder')
	const alertWrapper = document.createElement('div')
	alertWrapper.classList.add("alert");
	alertWrapper.classList.add(messageType);
	alertWrapper.classList.add("alert-dismissable");
	alertWrapper.innerHTML = [
		'	<div>'+messageContent+'</div>',
		'	<button type="button" class="btn-close" data-bs-dismiss="alert"></button>'
	].join('')

	alertPlaceholder.append(alertWrapper);
}

function deleteFromCart(cartProductId, cartIndex) {	
	updateProductCount(cartProductId, 0, cartIndex);
}

function updateProductCount(cartProductId, count, cartIndex) {
	var url = CONTEXT_PATH + '/updateProductCount?cartProductId=' + cartProductId
			+ '&productCount=' + count;

	const xhr = new XMLHttpRequest();

	xhr.onreadystatechange = function(data) {
		if(xhr.readyState !== 4) return;

		if(xhr.status === 200) {
			// update cart in header
			document.querySelector("#headerCartOverview .headerCartProductCount").textContent = data.headerCartOverview;

			// update total unit price
			document.querySelector('#product' + cartIndex +" .product-total-unit-price").textContent = data.currency + data.totalUnitPrice;

			//subtotal
			document.querySelector("#orderSubTotalValue").textContent = data.currency + data.subTotalPrice;
			//Tax
			document.querySelector("#orderSubTotalTaxValue").textContent = data.currency + data.totalTaxPrice;
			// update total price
			document.querySelector("#orderTotal").textContent = data.currency + data.totalPrice;
			
			// update mini cart
		} else {
			var errMsg = eval("(" + data.responseText + ")");
			sendAlert(errMsg, "alert-danger");
		}
	}
	xhr.open("GET", url);
	xhr.send();
}

// update price of product if the selected size has changed
function updatePrice(selectedField, productId) {
	var url = CONTEXT_PATH + '/updatePrice';
	
	const xhr = new XMLHttpRequest();

	xhr.onreadystatechange = function(data) {
		if(xhr.readyState !== 4) return;

		if(xhr.status === 200) {
			document.querySelector('#product-detail-form-price').textContent = data.newPrice;
		} else {
			var errMsg = eval("(" + data.responseText + ")");
			sendAlert(errMsg, "alert-danger");
		}
	}
	xhr.open("POST", url);
	xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xhr.send();
}

function updateProductOverview(data) {
    // Clear the existing product content
	var displayCase = document.querySelector('#productOverview .product-display-case-product-overview');
	while(displayCase.firstChild) displayCase.removeChild(displayCase.firstChild);
    // Iterate over the products in the data and append the updated content
	data.product.array.forEach(function(product) {
		var productCard = document.createElement('div');
		productCard.classList.add("card");
		productCard.classList.add("product-tile");
		productCard.innerHTML = [
			'<img src=${contextPath}'+product.imageURL+' class="card-img-top" alt="picture of '+product.name+'">',
			'<div class="card-body">',
			'	<h5 class="card-title">'+product.name+'</h5>',
			'	<p class="card-text product-tile-text">'+product.descriptionOverview+'</p>',
			'	<p class="card-text product-tile-price">'+product.minimumPrice+'</p>',
			'	<a href="${contextPath}/productDetail/${encodeURIComponent(product.name)}?productId=${product.id}" class="btn btn-primary">Buy here</a>',
			'</div>'
		].join('')
	});

    // Hide any remaining product tiles if needed
    /*for (var i = data.products.length; i < $('#productOverview .product-display-case-product-overview .card').length; i++) {
        $('#productOverview .product-display-case-product-overview .card:eq(' + i + ')').hide();
    }*/

    // Update the current page attribute
	document.getElementById("productOverview").setAttribute('currentPage', data.currentPage);
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
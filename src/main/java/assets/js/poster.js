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
		$('#product' + cartIndex +" .product-total-unit-price").text(data.currency + data.totalUnitPrice);

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
		$('#product-detail-form-price').text(data.newPrice);
	});
}

function updateProductOverview(data) {
    // Clear the existing product content
    $('#productOverview .product-display-case-product-overview').empty();
	var contextPath = CONTEXT_PATH;
    // Iterate over the products in the data and append the updated content
    $.each(data.products, function (index, product) {
        var productHTML = `
            <div class="card product-tile">
			<picture id="pagination-picture-${product.id}">
				<source media="(max-width: 399px)" srcset="${contextPath}${product.smallImageURL}">
				<source media="(max-width: 799px)" srcset="${contextPath}${product.mediumImageURL}">
				<source media="(max-width: 1999px)" srcset="${contextPath}${product.largeImageURL}">
				<img class="card-img-top" src="${contextPath}${product.originalImageURL}" alt="picture of ${product.name}" >
			</picture>
                <div class="card-body">
                    <h5 class="card-title">${product.name}</h5>
                    <p class="card-text product-tile-text">${product.descriptionOverview}</p>
                    <p class="card-text product-tile-price">$${product.minimumPrice}</p>
                    <a href="${contextPath}/productDetail/${encodeURIComponent(product.name)}?productId=${product.id}" class="btn btn-primary">Buy Here</a>
                </div>
            </div>
        `;

        $('#productOverview .product-display-case-product-overview').append(productHTML);
    });
	//------------------Next Prev Handling-------------------//
    // Remove existing "Previous" button
    $('#pagination-bottom #prev').remove();

    // Re-append "Previous" button at the beginning of pagination buttons
	
    var prevPageHTML = `
        <li class="page-item">
            <a id="prev" class="page-link" href="#" aria-label="Previous" data-prev="">
                <span aria-hidden="true">&laquo;</span>
            </a>
        </li>
    `;
    $('#pagination-bottom').prepend(prevPageHTML);
	if (data.currentPage ==1) {
		    // Remove existing "Previous" button
    $('#pagination-bottom #prev').remove();

	}
	// Remove existing "Next" button
    $('#pagination-bottom #next').remove();

    // Re-append "Next" button only if not on the last page
    if (data.currentPage < data.totalPages) {
        var nextPageHTML = `
            <li class="page-item">
                <a id="next" class="page-link" href="#" aria-label="Next" data-next="">
                    <span aria-hidden="true">&raquo;</span>
                </a>
            </li>
        `;
        $('#pagination-bottom').append(nextPageHTML);
    }
	$('#pagination-bottom a.page-link').removeClass('active');

	    // Add 'active' class to the page link corresponding to the current page
		var currentPageLink = $('#pagination-bottom a.page-link[data-page="' + data.currentPage + '"]');
		currentPageLink.addClass('active');

    // Hide any remaining product tiles if needed
    for (var i = data.products.length; i < $('#productOverview .product-display-case-product-overview .card').length; i++) {
        $('#productOverview .product-display-case-product-overview .card:eq(' + i + ')').hide();
    }

    // Update the current page attribute
    $('#productOverview').attr('currentPage', data.currentPage);
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
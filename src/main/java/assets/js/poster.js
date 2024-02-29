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
			var data = JSON.parse(xhr.responseText);
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
	console.log(productId, selectedField.value);
	const xhr = new XMLHttpRequest();

	xhr.onreadystatechange = function(data) {
		if(xhr.readyState !== 4) return;

		if(xhr.status === 200) {
			var data = JSON.parse(xhr.responseText);
			document.querySelector('#product-detail-form-price').textContent = data.newPrice;
		} else {
			var errMsg = eval("(" + data.responseText + ")");
			sendAlert(errMsg, "alert-danger");
		}
	}
	xhr.open("POST", url);
	xhr.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
	let dataJson = JSON.stringify({
		size: selectedField.value,
		productId: productId
	});
	console.log(dataJson);
	xhr.send(dataJson);
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
function postersSetup() {
	if(document.body.classList.contains('process')) {
		document.querySelector('.process').style.width = "33%";
	}
	document.getElementById('header-search-trigger').addEventListener('click', () => {
		document.getElementById('header-menu-search').style.display = 'block';
		document.getElementById('header-search-trigger').style.display = 'none';
	});
	document.getElementById('btnSearch').addEventListener('click', () => {
		document.getElementById('header-menu-search').style.display = 'block';
		document.getElementById('header-search-trigger').style.display = 'none';
	});
	//Sync search input of mobile and not mobile
	document.getElementById('s').addEventListener("keyup paste", () => {
		document.getElementById('sMobile').value = this.value;
	});
	document.getElementById('sMobile').addEventListener("keyup paste", () => {
		document.getElementById('s').value = this.value;
	});
	//Check if searchterm is Empty
	document.getElementById('searchForm').submit(function(){
		var input = document.getElementById('s').value.trim();
		if (!input){
			document.getElementById('s').value = input;
			return false;
		}
	})
	//Setup click handler for update and delete button
	if (document.getElementById('deleteProductModal').length) {
		document.querySelector('.btnUpdateProduct').addEventListener('click', () => {
			var cartProductId = this.data('id');
			var cartIndex = this.data('index');
			var inputValue = document.querySelector('#productCount' + cartIndex).value;

			if (inputValue == 0){
				this.siblings('.btnRemoveProduct').click();
			}
			else {
				document.querySelector('#productCount' + cartIndex).prop("defaultVaule", inputValue);
				updateProductCount(cartProductId, inputValue, cartIndex)
			}
		});

		//Setup handler for buttons of Confimation popup
		document.getElementById('deleteProductModal').addEventListener('show.bs.modal', (e) => {
			var cartProductId = document.querySelector(e.relatedTarget).data('id');
			var cartIndex = document.querySelector(e.relatedTarget).data('index');
			var prodInfo = document.querySelector(e.relatedTarget).closest('tr.cartOverviewProduct').find('.productInfo').clone();
			var oldValue = document.querySelector('#productCount' + document.querySelector(e.relatedTarget).data('index')).prop("defaultValue");
			//Rollback the input value if close button clicked
			document.getElementById('buttonClose').addEventListener('click', () => {
				document.querySelector('#productCount' + cartIndex).value = oldValue;
			});
			//Delete button
			this.querySelectorAll(`:scope ${'.btn-danger'}`).addEventListener('click', () => {
				deleteFromCart(cartProductId, cartIndex);
			});
			//Bodycontent of popup
			this.querySelectorAll(`:scope ${'.modal-body'}`).innerHTML = prodInfo;
		});
	}
}
function ready(postersSetup) {
	if (document.readyState !== 'loading') {
	  postersSetup();
	} else {
	  document.addEventListener('DOMContentLoaded', postersSetup);
	}
}  

const sendAlert = (messageContent, messageType) => {
	const alertPlaceholder = document.getElementById('alert-placeholder')
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
	var url = CONTEXT_PATH + '/' + LOCALE + '/updateProductCount?cartProductId=' + cartProductId
			+ '&productCount=' + count;

	const xhr = new XMLHttpRequest();

	xhr.onreadystatechange = function(data) {
		if(xhr.readyState !== 4) return;

		if(xhr.status === 200) {
			location.reload();
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
	var url = CONTEXT_PATH + '/' + LOCALE + '/updatePrice';
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
	xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    var params = "size=" + encodeURIComponent(selectedField.value) + "&productId=" + encodeURIComponent(productId);
    xhr.send(params);
}

function updateProductOverview(data) {
    // Clear the existing product content
	var displayCase = document.querySelector('#product-overview .product-display-case');
	while(displayCase.firstChild) displayCase.removeChild(displayCase.firstChild);
    // Iterate over the products in the data and append the updated content
	data.products.forEach(function(product) {
		var productCard = document.createElement('div');
		var productURI = encodeURI(product.name);
		productCard.classList.add("card");
		productCard.classList.add("product-tile");
		productCard.innerHTML = [
			`
			<a href="${CONTEXT_PATH}/${LOCALE}/productDetail/${productURI}?productId=${product.id}">
                <img
                    srcset="${CONTEXT_PATH}${product.smallImageURL} 400w,
                            ${CONTEXT_PATH}${product.mediumImageURL} 800w,
                            ${CONTEXT_PATH}${product.largeImageURL} 2000w"
                    sizes="262px"
                    src="${CONTEXT_PATH}${product.originalImageURL}"
                    alt="picture of ${product.localizedName}"
                    class="card-img-top"
                    id="pagination-picture-${product.id}">
            </a>
            <div class="card-body">
                <h5 class="card-title">${product.localizedName}</h5>
                <p class="card-text product-tile-text">${product.localizedDescriptionOverview}</p>
                <p class="card-text product-tile-price">$${product.minimumPrice}</p>
                <a href="${CONTEXT_PATH}/${LOCALE}/productDetail/${encodeURIComponent(product.name)}?productId=${product.id}" class="btn btn-primary">${data.BuyText}</a>
            </div>
        	`
		].join('')
		displayCase.appendChild(productCard);
	});
}

function updateProduct(productId, index, count) {
	if(count == 0) {
		deleteFromCart(productId, index);
	}
	else {
		updateProductCount(productId, count, index);
	};
}

//Setup on DOM ready
function postersSetup() {
	if(document.body.classList.contains('process')) {
		document.querySelector('.process').style.width = "33%";
	}
	if(document.getElementById('header-search-trigger')) {
		document.getElementById('header-search-trigger').addEventListener('click', () => {
			document.getElementById('header-menu-search').style.display = 'block';
			document.getElementById('header-search-trigger').style.display = 'none';
		});
	}
	if(document.getElementById('btnSearch')) {
		document.getElementById('btnSearch').addEventListener('click', () => {
			document.getElementById('header-menu-search').style.display = 'block';
			document.getElementById('header-search-trigger').style.display = 'none';
		});
	}
	//Sync search input of mobile and not mobile
	if(document.getElementById('s')) {
		document.getElementById('s').addEventListener("keyup paste", () => {
			document.getElementById('sMobile').value = this.value;
		});
	}
	if(document.getElementById('sMobile')) {
		document.getElementById('sMobile').addEventListener("keyup paste", () => {
			document.getElementById('s').value = this.value;
		});
	}
	//Check if searchterm is Empty
	if(document.getElementById('searchForm')) {
		document.getElementById('searchForm').submit(function(){
			var input = document.getElementById('s').value.trim();
			if (!input){
				document.getElementById('s').value = input;
				return false;
			}
		})
	}	
	//Setup click handler for update and delete
	if (document.getElementById('delete-product-modal')) {
		//Setup handler for buttons of delete product confimation popup
		document.getElementById('delete-product-modal').addEventListener('show.bs.modal', (e) => {
			var cartProductId = e.relatedTarget.getAttribute('data-id');
			var cartIndex = e.relatedTarget.getAttribute('data-index');
			var prodInfo = e.relatedTarget.closest('tr.js-cart-product').querySelector('.js-cart-product-description').cloneNode(true);
			//Delete button
			console.log(e.target);
			console.log(e.relatedTarget);
			e.target.querySelector("#button-delete").addEventListener('click', () => {
				deleteFromCart(cartProductId, cartIndex);
			});
			// dismiss button
			e.target.querySelector("#button-close").addEventListener('click', () => {
				e.target.querySelector(`:scope ${'.modal-body'}`).innerHTML = '';
				e.target.querySelector('.modal-footer').innerHTML += '';
			});
			// modal close button (clone of the above since it should work the same)
			e.target.querySelector(".btn-close").addEventListener('click', () => {
				e.target.querySelector(`:scope ${'.modal-body'}`).innerHTML = '';
				e.target.querySelector('.modal-footer').innerHTML += '';
			});
			//Bodycontent of popup
			e.target.querySelector(`:scope ${'.modal-body'}`).appendChild(prodInfo);
		});
	}
}

window.addEventListener("DOMContentLoaded", postersSetup());

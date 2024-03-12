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
	xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    var params = "size=" + encodeURIComponent(selectedField.value) + "&productId=" + encodeURIComponent(productId);
    xhr.send(params);
}

function updateProductOverview(data) {
    // Clear the existing product content
	var displayCase = document.querySelector('#productOverview .product-display-case-product-overview');
	while(displayCase.firstChild) displayCase.removeChild(displayCase.firstChild);
    // Iterate over the products in the data and append the updated content
	data.products.forEach(function(product) {
		var productCard = document.createElement('div');
		productCard.classList.add("card");
		productCard.classList.add("product-tile");
		productCard.innerHTML = [
			`
            <div class="card product-tile">
			<picture id="pagination-picture-${product.id}">
				<source media="(max-width: 399px)" srcset="${CONTEXT_PATH}${product.smallImageURL}">
				<source media="(max-width: 799px)" srcset="${CONTEXT_PATH}${product.mediumImageURL}">
				<source media="(max-width: 1999px)" srcset="${CONTEXT_PATH}${product.largeImageURL}">
				<img class="card-img-top" src="${CONTEXT_PATH}${product.originalImageURL}" alt="picture of ${product.name}" >
			</picture>
                <div class="card-body">
                    <h5 class="card-title">${product.name}</h5>
                    <p class="card-text product-tile-text">${product.descriptionOverview}</p>
                    <p class="card-text product-tile-price">$${product.minimumPrice}</p>
                    <a href="${CONTEXT_PATH}/productDetail/${encodeURIComponent(product.name)}?productId=${product.id}" class="btn btn-primary">Buy Here</a>
                </div>
            </div>
        	`
		].join('')
		displayCase.appendChild(productCard);
	});
	//------------------Next Prev Handling-------------------//
    // Remove existing "Previous" button
	if(document.querySelector('#pagination-bottom #prev')) {
		document.querySelector('#pagination-bottom #prev').parentElement.removeChild(document.querySelector('#pagination-bottom #prev'));
	}

    // Re-append "Previous" button at the beginning of pagination buttons
	var prevPage = document.createElement('li');
	prevPage.classList.add('page-item');
	var prevPageInnerHTML = `
        <a id="prev" class="page-link" href="#" aria-label="Previous" data-prev="">
            <span aria-hidden="true">&laquo;</span>
        </a>
    `;
	prevPage.innerHTML = prevPageInnerHTML;
	document.getElementById('pagination-bottom').prepend(prevPage);

	if (data.currentPage ==1) {
		// Remove existing "Previous" button
    	document.querySelector('#pagination-bottom #prev').parentElement.removeChild(document.querySelector('#pagination-bottom #prev'));
	}

	// Remove existing "Next" button
    if(document.querySelector('#pagination-bottom #next')) {
		document.querySelector('#pagination-bottom #next').parentElement.removeChild(document.querySelector('#pagination-bottom #next'));
	}

    // Re-append "Next" button only if not on the last page
    if (data.currentPage < data.totalPages) {
		var nextPage = document.createElement('li');
		nextPage.classList.add('page-item');
        var nextPageInnerHTML = `
            <a id="next" class="page-link" href="#" aria-label="Next" data-next="">
                <span aria-hidden="true">&raquo;</span>
            </a>
        `;
		nextPage.innerHTML = nextPageInnerHTML;
        document.querySelector('#pagination-bottom').append(nextPage);
    }

	document.querySelector('#pagination-bottom a.page-link').classList.remove('active');

	// Add 'active' class to the page link corresponding to the current page
	var currentPageLink = document.querySelector('#pagination-bottom a.page-link[data-page="' + data.currentPage + '"]');
	currentPageLink.classList.add('active');

    // Hide any remaining product tiles if needed
    /*for (var i = data.products.length; i < $('#productOverview .product-display-case-product-overview .card').length; i++) {
        $('#productOverview .product-display-case-product-overview .card:eq(' + i + ')').hide();
    }*/

    // Update the current page attribute
	document.getElementById("productOverview").setAttribute('currentPage', data.currentPage);
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
	if (document.getElementById('deleteProductModal')) {
		//Setup handler for buttons of delete product confimation popup
		document.getElementById('deleteProductModal').addEventListener('show.bs.modal', (e) => {
			var cartProductId = e.relatedTarget.getAttribute('data-id');
			var cartIndex = e.relatedTarget.getAttribute('data-index');
			var prodInfo = e.relatedTarget.closest('tr.cart-product').querySelector('.cart-product-description').cloneNode(true);
			//Delete button
			console.log(e.target);
			console.log(e.relatedTarget);
			e.target.querySelector("#buttonDelete").addEventListener('click', () => {
				deleteFromCart(cartProductId, cartIndex);
			});
			//Bodycontent of popup
			e.target.querySelector(`:scope ${'.modal-body'}`).appendChild(prodInfo);
		});
	}
}

window.addEventListener("DOMContentLoaded", postersSetup());

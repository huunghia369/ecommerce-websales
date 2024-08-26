<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglib.jsp"%>

<div class="product-list">
<c:forEach var="p" items="${list}">
	<div class="product-item col-md-4 col-sm-6 nn-prod" data-id="${p.id}" data-name="${p.name}" data-image="${p.image}" data-unitPrice="${p.unitPrice}" data-discount="${p.discount}" data-available="${p.available}">
		<div class="panel panel-success">
			<div class="panel-heading text-center">
				<h4 class="panel-title">${p.name}</h4>
			</div>
			<div class="panel-body text-center">
				<a href="/product/detail/${p.id}"><img src="/static/images/products/${p.image}"/></a>
			</div>
			<div class="panel-footer">
				<div class="row">
					<div class="col-xs-3"><f:formatNumber value="${p.unitPrice}" pattern="#,###.00" />đ</div>
					<div class="col-xs-9 text-right">
						<%@include file="btn-prod.jsp" %>
					</div>
				</div>
			</div>
			<c:choose>
				<c:when test="${p.discount > 0 && p.available}">
					<img src="/static/images/promo-icon-14230-Windows.ico" style="animation: pulseGlow 1s infinite alternate;"/>
				</c:when>
				<c:when test="${p.available}">
					<img src="/static/images/special-icon.gif" style="animation: pulseGlow 1s infinite alternate;"/>
				</c:when>
				<c:otherwise>
					<p style="color: red; font-weight: bold; animation: blink 1s infinite; position: absolute; bottom: 0; right: 110px;">Tạm hết hàng</p>
				</c:otherwise>
			</c:choose>
			<style>
				@keyframes blink {
					0%, 100% { opacity: 1; }
					50% { opacity: 0.5; }
				}

				@keyframes pulseGlow {
					0% {
						transform: scale(1);
						filter: brightness(1);
					}
					50% {
						transform: scale(1.1);
						filter: brightness(1.5);
					}
					100% {
						transform: scale(1);
						filter: brightness(1);
					}
				}
			</style>
		</div>
	</div>
</c:forEach>
</div>
<div class="pagination-container">
	<ul id="pagination-demo" class="pagination-lg"></ul>
</div>
<script src="/static/slideshow/js/modernizr.custom.63321.js"></script>
<script src="/static/slideshow/js/jquery.catslider.js"></script>
<link href="/static/slideshow/css/catslider.css" rel="stylesheet" />

<script>
	$(document).ready(function() {
		// Khởi tạo phân trang
		var itemsPerPage = 9; // Số sản phẩm trên mỗi trang
		var items = $('.product-item');
		var totalPages = Math.ceil(items.length / itemsPerPage);
		var currentPage = 1;

		function showPage(page) {
			items.hide();
			items.slice((page - 1) * itemsPerPage, page * itemsPerPage).show();
		}

		$('#pagination-demo').twbsPagination({
			totalPages: totalPages,
			visiblePages: 10,
			startPage: currentPage,
			onPageClick: function (event, page) {
				currentPage = page;
				showPage(page);
			}
		});

		// Khởi tạo slide show sau khi phân trang được khởi tạo
		showCatSlider('.mi-slider', 5000);
	});
</script>

<%@include file="dialog.jsp" %>


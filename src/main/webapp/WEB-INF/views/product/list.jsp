<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglib.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="product-list">
    <c:forEach var="p" items="${list}">
        <div class="product-item col-md-4 col-sm-6 nn-prod" data-id="${p.id}" data-name="${p.name}" data-image="${p.image}" data-unitPrice="${p.unitPrice}" data-discount="${p.discount}" data-available="${p.available}">
            <div class="card-product">
                <div class="card-image-product text-center">
                    <a href="/product/detail/${p.id}">
                        <img src="/static/images/products/${p.image}" alt="${p.name}" class="product-image" />
                    </a>	
					<c:choose>
						<c:when test="${p.discount > 0 && p.available}">
							<span class="discount-badge">- ${fn:escapeXml(Math.round(p.discount * 100))}%</span>
						</c:when>
						<c:when test="${p.available}">
							<img src="/static/images/special-icon.gif" class="special-icon" />
						</c:when>
                	</c:choose>				
                </div>
                <div class="card-body-product">
                    <h5 class="product-title">${p.name}</h5>
                    <div class="d-flex justify-content-between align-items-center">
                        <c:choose>
                            <c:when test="${!p.available}">
                                <p class="out-of-stock-label">Tạm hết hàng</p>
                            </c:when>
							<c:when test="${p.discount > 0}">
								<p class="price">
									<span class="original-price"><f:formatNumber value="${p.unitPrice}" pattern="#,###.00" /> đ</span>
									<span class="discounted-price">
										<f:formatNumber value="${p.unitPrice * (1 - p.discount)}" pattern="#,###.00" /> đ
									</span>
								</p>
							</c:when>
							<c:otherwise>
								<p class="price"><f:formatNumber value="${p.unitPrice}" pattern="#,###.00" /> đ</p>
							</c:otherwise>
                        </c:choose>
                        <div class="text-right">
                            <%@include file="btn-prod.jsp" %>
                        </div>
                    </div>
                </div>
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
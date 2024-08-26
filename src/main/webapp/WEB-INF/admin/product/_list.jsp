<%@ page pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>

<div class="panel panel-default">
	<div class="panel-heading">
		<form action="/admin/product/index#list" method="get">
			<div class="input-group">
				<label class="input-group-addon">Category: </label>
				<select name="category_id" class="form-control" onchange="this.form.submit()">
					<option value="0" ${param.category_id == null || param.category_id == '0' ? 'selected' : ''}>All Categories</option>
					<c:forEach var="c" items="${cates}">
						<option value="${c.id}" ${param.category_id != null && param.category_id != '0' && param.category_id == c.id ? 'selected' : ''}>${c.nameVN}</option>
					</c:forEach>
				</select>
			</div>
		</form>
	</div>
	<div class="table-container">
		<table class="table table-hover">
			<thead>
			<tr>
				<th colspan="9" class="position-relative">
					<form class="position-absolute end-0 top-0 mb-0" style="width: 233px;">
						<input name="searchInput" id="myInput"
							   class="form-control" type="search" placeholder="Search"
							   aria-label="Search">
					</form>
				</th>
			</tr>
			<tr>
				<th><a href="#" class="sort" data-sort="id">Id</a></th>
				<th><a href="#" class="sort" data-sort="name">Tên</a></th>
				<th><a href="#" class="sort" data-sort="unitPrice">Giá</a></th>
				<th><a href="#" class="sort" data-sort="availableQuantity">Tồn kho</a></th>
				<th><a href="#" class="sort" data-sort="quantity">Số lượng đang giao</a></th>
				<th><a href="#" class="sort" data-sort="soldQuantity">Số lượng đã bán</a></th>
				<th><a href="#" class="sort" data-sort="discount">Khuyến mãi</a></th>
				<th><a href="#" class="sort" data-sort="available">Trạng thái hàng</a></th>
				<th><a href="#" class="sort" data-sort="category">Loại</a></th>
				<th></th>
			</tr>
			</thead>
			<tbody id="myTable">
			<c:forEach var="item" items="${list}">
				<c:if test="${param.category_id == null || param.category_id == '0' || item.category.id == param.category_id}">
					<tr class="product-item">
						<td>${item.id}</td>
						<td>${item.name}</td>
						<td>${item.unitPrice} đ</td>
						<td style="text-align: center;
								color: ${item.availableQuantity < 10 ? 'red' : 'black'};">
								${item.availableQuantity}
						</td>
						<td style="text-align: center;">${item.quantity - item.availableQuantity}</td>
						<td class="sold-quantity" style="text-align: center;">${item.soldQuantity}</td>
						<td>${item.discount * 100}%</td>
						<td>${item.available ? 'Yes' : 'No'}</td>
						<td>${item.category.nameVN}</td>
						<td class="text-center">
							<a href="${prefix}/edit/${item.id}" class="btn btn-sm btn-info">
								<span class="glyphicon glyphicon-edit"></span>
							</a>
							<a href="${prefix}/delete/${item.id}" class="btn btn-sm btn-danger" onclick="return confirmDelete(event)">
								<span class="glyphicon glyphicon-trash"></span>
							</a>
						</td>
					</tr>
				</c:if>
			</c:forEach>
			</tbody>
		</table>
	</div>
	<div class="pagination-container">
		<ul id="pagination-demo" class="pagination-lg"></ul>
	</div>
</div>
<script>
	$(document).ready(function() {
		// Khởi tạo phân trang
		var itemsPerPage = 10; // Số sản phẩm trên mỗi trang
		var items = $('.product-item');
		var totalPages = Math.ceil(items.length / itemsPerPage);
		var currentPage = 1;

		var soldQuantityCells = document.querySelectorAll('.sold-quantity');
		var maxSoldQuantity = 0;

		soldQuantityCells.forEach(function(cell) {
			var value = parseInt(cell.textContent, 10);
			if (value > maxSoldQuantity) {
				maxSoldQuantity = value;
			}
		});

		// Thay đổi màu sắc của ô chứa giá trị lớn nhất
		soldQuantityCells.forEach(function(cell) {
			var value = parseInt(cell.textContent, 10);
			if (value === maxSoldQuantity) {
				cell.style.color = 'green';
				cell.style.fontWeight = 'bold';
			} else {
				cell.style.color = 'black';
			}
		});

		function showPage(page) {
			items.hide();
			items.slice((page - 1) * itemsPerPage, page * itemsPerPage).show();
		}

		function sortTable(columnIndex, order) {
			var rows = $('.product-item').get();

			rows.sort(function(a, b) {
				var aText = $(a).find('td').eq(columnIndex).text().trim();
				var bText = $(b).find('td').eq(columnIndex).text().trim();

				// Nếu cột là số, so sánh số
				if (columnIndex === 2 || columnIndex === 3 || columnIndex === 4 || columnIndex === 5 || columnIndex === 6) {
					aText = parseFloat(aText.replace(/[^0-9.-]+/g, ""));
					bText = parseFloat(bText.replace(/[^0-9.-]+/g, ""));
					return (order === 'asc' ? aText - bText : bText - aText);
				}

				// Nếu cột là văn bản, so sánh chuỗi
				return (order === 'asc' ? aText.localeCompare(bText) : bText.localeCompare(aText));
			});

			$.each(rows, function(index, row) {
				$('#myTable').append(row);
			});
		}

		function updatePagination() {
			var totalItems = $('.product-item').length;
			var totalPages = Math.ceil(totalItems / itemsPerPage);

			$('#pagination-demo').twbsPagination('destroy');
			$('#pagination-demo').twbsPagination({
				totalPages: totalPages,
				visiblePages: 10,
				startPage: currentPage,
				onPageClick: function (event, page) {
					currentPage = page;
					showPage(page);
				}
			});

			showPage(currentPage);
		}

		$('.sort').click(function(e) {
			e.preventDefault();
			var columnIndex = $(this).parent().index();
			var order = $(this).hasClass('asc') ? 'desc' : 'asc';

			$('.sort').removeClass('asc desc');
			$(this).addClass(order);

			sortTable(columnIndex, order);
			updatePagination();
		});

		$('#pagination-demo').twbsPagination({
			totalPages: totalPages,
			visiblePages: 10,
			startPage: currentPage,
			onPageClick: function (event, page) {
				currentPage = page;
				showPage(page);
			}
		});

		// Hiển thị trang đầu tiên
		showPage(currentPage);
	});
	function confirmDelete(event) {
		const confirmed = confirm('Are you sure you want to delete this item?');
		if (!confirmed) {
			event.preventDefault();
		}
		return confirmed;
	}
	$("#myInput").on("keyup", function() {var value = $(this).val().toLowerCase();$("#myTable tr").filter(function() {$(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)});
		// Sau khi tìm kiếm, cập nhật phân trang
		showPage(1);
		$('#pagination-demo').twbsPagination('destroy');
		$('#pagination-demo').twbsPagination({
			totalPages: Math.ceil($('.product-item:visible').length / itemsPerPage),
			visiblePages: 10,
			startPage: 1,
			onPageClick: function (event, page) {
				currentPage = page;
				showPage(page);
			}
		});
	})
</script>



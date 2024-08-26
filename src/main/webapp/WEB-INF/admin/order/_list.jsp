<%@ page pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="f"%>

<div class="panel panel-default">

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
					<th class="sortable" data-column="0">Id</th>
					<th class="sortable" data-column="1">Khách hàng</th>
					<th class="sortable" data-column="2">Địa chỉ</th>
					<th class="sortable" data-column="3">SĐT</th>
					<th class="sortable" data-column="4">Ngày đặt</th>
					<th class="sortable" data-column="5">Tổng tiền</th>
					<th class="sortable" data-column="6">Trạng thái</th>
					<th></th>
					<th></th>
				</tr>
			</thead>
			<tbody id="myTable">
				<c:forEach var="item" items="${list}">
					<tr class="order-item">
						<td>${item.id}</td>
						<td>${item.customer.fullname}</td>
						<td>${item.address}</td>
						<td>${item.customer.phone}</td>
						<td>${item.orderDate}</td>
						<td><f:formatNumber value="${item.amount}" pattern="#,###.00" /> đ</td>
						<td style="color: ${item.status == 0 ? 'green' :
								item.status == 1 ? 'blue' :
										item.status == 2 ? 'orange' :
												item.status == 3 ? 'purple' : 'red'};">
							<c:choose>
								<c:when test="${item.status == 0}">Hoàn tất</c:when>
								<c:when test="${item.status == 1}">Chờ xác nhận</c:when>
								<c:when test="${item.status == 2}">Đang giao</c:when>
								<c:when test="${item.status == 3}">Đã giao</c:when>
								<c:when test="${item.status == 4}">Đã hủy</c:when>
							</c:choose>
						</td>
						<td>
							<c:if test="${item.status == 1}">
								<form action="${prefix}/update-status/${item.id}" method="post">
									<input type="hidden" name="_method" value="post">
									<button type="submit" class="btn btn-sm btn-success">
										Duyệt
									</button>
								</form>
							</c:if>
						</td>
						<td class="text-center">
							<a href="${prefix}/edit/${item.id}" class="btn btn-sm btn-info"> <span
								class="glyphicon glyphicon-edit"></span></a>
							<a href="${prefix}/delete/${item.id}" class="btn btn-sm btn-danger"> <span
								class="glyphicon glyphicon-trash"></span></a>
						</td>
					</tr>
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
		var itemsPerPage = 10; // Số đơn hàng trên mỗi trang
		var items = $('.order-item');
		var totalPages = Math.ceil(items.length / itemsPerPage);
		var currentPage = 1;

		function showPage(page) {
			items.hide();
			items.slice((page - 1) * itemsPerPage, page * itemsPerPage).show();
		}

		function updatePagination() {
			var totalItems = $('.order-item').length;
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

		function sortTable(columnIndex, order) {
			var rows = $('.order-item').get();

			rows.sort(function(a, b) {
				var aText = $(a).find('td').eq(columnIndex).text().trim();
				var bText = $(b).find('td').eq(columnIndex).text().trim();

				// Chuyển đổi dữ liệu cho các cột ngày và số tiền
				if (columnIndex === 4) { // Ngày đặt
					aText = new Date(aText);
					bText = new Date(bText);
					return (order === 'asc' ? aText - bText : bText - aText);
				}
				if (columnIndex === 5) { // Tổng tiền
					aText = parseFloat(aText.replace(/[^0-9.-]+/g, ""));
					bText = parseFloat(bText.replace(/[^0-9.-]+/g, ""));
					return (order === 'asc' ? aText - bText : bText - aText);
				}

				// Xử lý các cột văn bản khác
				return (order === 'asc' ? aText.localeCompare(bText) : bText.localeCompare(aText));
			});

			$.each(rows, function(index, row) {
				$('#myTable').append(row);
			});
		}

		function handleSort(columnIndex) {
			var sortOrder = $(this).data('order') === 'asc' ? 'desc' : 'asc';
			$('.sortable').removeClass('asc desc');
			$(this).addClass(sortOrder);
			$(this).data('order', sortOrder);

			sortTable(columnIndex, sortOrder);
			updatePagination();
		}

		$('.sortable').on('click', function(event) {
			event.preventDefault(); // Ngăn chặn hành động mặc định của liên kết
			var columnIndex = $(this).data('column');
			handleSort.call(this, columnIndex);
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
	$("#myInput").on("keyup", function() {var value = $(this).val().toLowerCase();$("#myTable tr").filter(function() {$(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)});
		// Sau khi tìm kiếm, cập nhật phân trang
		showPage(1);
		$('#pagination-demo').twbsPagination('destroy');
		$('#pagination-demo').twbsPagination({
			totalPages: Math.ceil($('.order-item:visible').length / itemsPerPage),
			visiblePages: 10,
			startPage: 1,
			onPageClick: function (event, page) {
				currentPage = page;
				showPage(page);
			}
		});
	})
</script>



<%@ page pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>

<div class="panel panel-default">
	<div class="table-container">
		<table class="table table-hover">
			<thead>
				<tr>
					<th>Mã</th>
					<th>Mật khẩu</th>
					<th>Họ tên</th>
					<th>Email</th>
					<td>SĐT</td>
					<th>Hình ảnh</th>
					<th>Kích hoạt ?</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
			<c:forEach var="item" items="${users}">
				<tr class="user-item">
					<td>${item.id}</td>
					<td>${item.password}</td>
					<td>${item.fullname}</td>
					<td>${item.email}</td>
					<td>${item.phone}</td>
					<td><img  src="/static/images/customers/${item.photo}" style="width: 50px"></td>
					<td>${item.activated?'Yes':'No'}</td>
					<td class="text-center">
						<a href="${prefix}/edit/${item.id}" class="btn btn-sm btn-info">
							<span class="glyphicon glyphicon-edit"></span>
						</a>
						<a href="${prefix}/delete/${item.id}" class="btn btn-sm btn-danger">
							<span class="glyphicon glyphicon-trash"></span>
						</a>
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
		var itemsPerPage = 10; // Số người dùng trên mỗi trang
		var items = $('.user-item');
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

		// Hiển thị trang đầu tiên
		showPage(currentPage);
	});
</script>
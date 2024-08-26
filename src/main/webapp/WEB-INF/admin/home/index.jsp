<%@ page pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
	.panel-heading {
		background-color: #f5f5f5;
		color: #333;
		font-size: 18px;
	}
	.panel-body {
		background-color: #fff;
	}
	.panel-footer {
		background-color: #f5f5f5;
	}
	.table thead th {
		background-color: #f8f8f8;
	}
	.table tbody tr:nth-of-type(even) {
		background-color: #f9f9f9;
	}
	.table tbody tr:hover {
		background-color: #f1f1f1;
	}
	.panel-title {
		font-weight: bold;
	}
	.chart-container {
		position: relative;
		height: 400px;
		width: 100%;
	}
	.arrow-up {
		display: inline-block;
		width: 0;
		height: 0;
		border-left: 5px solid transparent;
		border-right: 5px solid transparent;
		border-bottom: 10px solid green;
		margin-right: 5px;
	}

	.arrow-down {
		display: inline-block;
		width: 0;
		height: 0;
		border-left: 5px solid transparent;
		border-right: 5px solid transparent;
		border-top: 10px solid red;
		margin-right: 5px;
	}
</style>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-4">
			<div class="panel panel-danger">
				<div class="panel-heading text-center">
					<h3 class="panel-title">Hàng tồn kho thấp</h3>
				</div>
				<div class="panel-body">
					<table class="table table-striped table-bordered">
						<thead>
						<tr>
							<th>Tên sản phẩm</th>
							<th>Số lượng tồn</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="${lowStockProducts}" var="product">
							<tr>
								<td>${product.name}</td>
								<td>${product.quantity}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="panel-footer text-center">
					<button class="btn btn-danger" data-toggle="modal" data-target="#updateModal">
						Nhập hàng ngay
					</button>
				</div>
			</div>

			<!-- Modal -->
			<div id="updateModal" class="modal fade" tabindex="-1" role="dialog">
				<div class="modal-dialog" role="document">
					<div class="modal-content">
						<div class="modal-header bg-danger text-white">
							<h2 class="modal-title mx-auto text-center"><strong>Cập nhật số lượng hàng tồn kho</strong></h2>
							<button type="button" class="close" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<form id="updateStockForm" method="post" action="${pageContext.request.contextPath}/update-stock">
							<div class="modal-body">
								<c:forEach items="${lowStockProducts}" var="product">
									<div class="form-group row mb-3">
										<label for="quantity_${product.id}" class="col-sm-10 col-form-label font-weight-bold">${product.name}</label>
										<div class="col-sm-2">
											<input type="number" class="form-control" id="quantity_${product.id}" name="quantity_${product.id}" value="${product.quantity}" min="${product.quantity}" required>
										</div>
									</div>
								</c:forEach>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
								<button type="submit" class="btn btn-primary">Lưu thay đổi</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>

		<div class="col-md-4">
			<div class="panel panel-primary">
				<div class="panel-heading text-center">
					<h3 class="panel-title">Top sản phẩm bán chạy</h3>
				</div>
				<div class="panel-body">
					<table class="table table-striped table-bordered">
						<thead>
						<tr>
							<th>Tên sản phẩm</th>
							<th>Số lượng bán</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="${topSellingProducts}" var="product">
							<tr>
								<td>${product.name}</td>
								<td>${product.soldQuantity}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="panel-footer text-center">
					<div class="panel-footer text-center">
						<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#profitLossModal">
							Xem thống kê hôm nay
						</button>
					</div>
				</div>
			</div>

			<div class="modal fade" id="profitLossModal" tabindex="-1" role="dialog" aria-labelledby="profitLossModalLabel" aria-hidden="true">
				<div class="modal-dialog modal-lg" role="document">
					<div class="modal-content">
						<div class="modal-header bg-primary text-white">
							<h3 class="modal-title mx-auto text-center" id="profitLossModalLabel"><strong>THỐNG KÊ LỢI NHUẬN - THUA LỖ</strong></h3>
							<button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body">
							<h3 class="text-primary text-center">Báo cáo hàng ngày</h3>
							<div class="container-fluid">
								<div class="row">
									<div class="col-md-6">
										<div class="card">
											<div class="card-header bg-primary text-white text-center">
												<strong>Lợi nhuận và Thua lỗ hôm nay</strong>
											</div>
											<div class="card-body">
												<canvas id="profitLossChart"></canvas>
												<script>
													var ctx = document.getElementById('profitLossChart').getContext('2d');
													var profitLossChart = new Chart(ctx, {
														type: 'doughnut',
														data: {
															labels: ['Lợi nhuận', 'Thua lỗ'],
															datasets: [{
																label: 'Số tiền',
																data: [${profitToday}, ${-lossToday}],
																backgroundColor: ['#28a745', '#dc3545'],
																borderColor: ['#fff', '#fff'],
																borderWidth: 1
															}]
														},
														options: {
															maintainAspectRatio: false,
															aspectRatio: 1,
															responsive: true,
															plugins: {
																legend: {
																	position: 'top',
																},
																tooltip: {
																	callbacks: {
																		label: function(tooltipItem) {
																			return tooltipItem.label + ': ' + tooltipItem.raw.toLocaleString() + ' VND';
																		}
																	}
																}
															}
														}
													});
												</script>
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class="row">
											<div class="col-md-6 mb-3">
												<div class="card">
													<div class="card-header bg-success text-light">
														<strong>Lợi nhuận</strong>
													</div>
													<div class="card-body">
														<h4 class="text-muted">${profitToday} VND</h4>
													</div>
												</div>
											</div>
											<div class="col-md-6 mb-3">
												<div class="card">
													<div class="card-header bg-danger text-light">
														<strong>Thua lỗ</strong>
													</div>
													<div class="card-body">
														<h4 class="text-muted">${lossToday} VND</h4>
													</div>
												</div>
											</div>
											<div class="col-md-6 mb-3">
												<div class="card">
													<div class="card-header bg-primary text-light">
														<strong>Số lượng bán</strong>
													</div>
													<div class="card-body">
														<h4 class="text-muted">${quantitySoldToday}</h4>
													</div>
												</div>
											</div>
											<div class="col-md-6 mb-3">
												<div class="card">
													<div class="card-header bg-warning text-dark">
														<strong>Số lượng đang giao</strong>
													</div>
													<div class="card-body">
														<h4 class="text-muted">${quantityInTransitToday}</h4>
													</div>
												</div>
											</div>
											<div class="col-md-6 mb-3">
												<div class="card">
													<div class="card-header bg-info text-light">
														<strong>Số lượng truy cập</strong>
													</div>
													<div class="card-body">
														<h4 class="text-muted">${totalAccesses}</h4>
													</div>
												</div>
											</div>
											<div class="col-md-6 mb-3">
												<div class="card">
													<div class="card-header bg-secondary text-light">
														<strong>Số khách đang hoạt động</strong>
													</div>
													<div class="card-body">
														<h4 class="text-muted">${activeCustomers}</h4>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div style="border-bottom: 2px solid #ccc; margin: 20px 0;"></div>
							<h3 class="text-primary text-center">Báo cáo hàng tuần</h3>
							<table class="table table-bordered">
								<thead>
								<tr class="table-primary">
									<th>Chỉ tiêu</th>
									<th>Tuần này</th>
									<th>Tuần trước</th>
									<th>Chênh lệch</th>
									<th>Trạng thái</th>
								</tr>
								</thead>
								<tbody>
								<tr>
									<td><strong>Lợi nhuận</strong></td>
									<td>${totalCurrentWeekProfit} VNĐ</td>
									<td>${totalPreviousWeekProfit} VNĐ</td>
									<td>
										<c:choose>
											<c:when test="${weekProfitDifference > 0}">
												${weekProfitDifference} VNĐ <span class="arrow-up"></span>
											</c:when>
											<c:when test="${weekProfitDifference < 0}">
												${weekProfitDifference} VNĐ <span class="arrow-down"></span>
											</c:when>
											<c:otherwise>
												${weekProfitDifference} VNĐ
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${weekProfitDifference > 0}">
												<span class="text-success">Tăng</span>
											</c:when>
											<c:when test="${weekProfitDifference < 0}">
												<span class="text-danger">Giảm</span>
											</c:when>
										</c:choose>
									</td>
								</tr>
								<tr>
									<td><strong>Thua lỗ</strong></td>
									<td>${totalCurrentWeekLoss} VNĐ</td>
									<td>${totalPreviousWeekLoss} VNĐ</td>
									<td>
										<c:choose>
											<c:when test="${weekLossDifference < 0}">
												${weekLossDifference} VNĐ <span class="arrow-up"></span>
											</c:when>
											<c:when test="${weekLossDifference > 0}">
												${weekLossDifference} VNĐ <span class="arrow-down"></span>
											</c:when>
											<c:otherwise>
												${weekLossDifference} VNĐ
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${weekLossDifference < 0}">
												<span class="text-success">Giảm</span>
											</c:when>
											<c:when test="${weekLossDifference > 0}">
												<span class="text-danger">Tăng</span>
											</c:when>
										</c:choose>
									</td>
								</tr>
								</tbody>
							</table>
							<div style="border-bottom: 2px solid #ccc; margin: 20px 0;"></div>
							<h3 class="text-primary text-center">Báo cáo hàng tháng</h3>
							<table class="table table-bordered">
								<thead>
								<tr class="table-primary">
									<th>Chỉ tiêu</th>
									<th>Tháng này</th>
									<th>Tháng trước</th>
									<th>Chênh lệch</th>
									<th>Trạng thái</th>
								</tr>
								</thead>
								<tbody>
								<tr>
									<td class><strong>Lợi nhuận</strong></td>
									<td>${totalCurrentMonthProfit} VNĐ</td>
									<td>${totalPreviousMonthProfit} VNĐ</td>
									<td>
										<c:choose>
											<c:when test="${totalCurrentMonthProfit > totalPreviousMonthProfit}">
												${profitDifference} VNĐ <span class="arrow-up"></span>
											</c:when>
											<c:when test="${totalCurrentMonthProfit < totalPreviousMonthProfit}">
												${profitDifference} VNĐ <span class="arrow-down"></span>
											</c:when>
											<c:otherwise>
												${profitDifference} VNĐ
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${profitDifference > 0}">
												<span class="text-success">Tăng</span>
											</c:when>
											<c:when test="${profitDifference < 0}">
												<span class="text-danger">Giảm</span>
											</c:when>
										</c:choose>
									</td>
								</tr>
								<tr>
									<td class><strong>Thua lỗ</strong></td>
									<td>${totalCurrentMonthLoss} VNĐ</td>
									<td>${totalPreviousMonthLoss} VNĐ</td>
									<td>
										<c:choose>
											<c:when test="${totalCurrentMonthLoss < totalPreviousMonthLoss}">
												${lossDifference} VNĐ <span class="arrow-up"></span>
											</c:when>
											<c:when test="${totalCurrentMonthLoss > totalPreviousMonthLoss}">
												${lossDifference} VNĐ <span class="arrow-down"></span>
											</c:when>
											<c:otherwise>
												${lossDifference} VNĐ
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${lossDifference > 0}">
												<span class="text-danger">Tăng</span>
											</c:when>
											<c:when test="${lossDifference < 0}">
												<span class="text-success">Giảm</span>
											</c:when>
										</c:choose>
									</td>
								</tr>
								</tbody>
							</table>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="col-md-4">
			<div class="panel panel-success">
				<div class="panel-heading text-center">
					<h3 class="panel-title">Top khách hàng</h3>
				</div>
				<div class="panel-body">
					<table class="table table-striped table-bordered">
						<thead>
						<tr>
							<th>Tên</th>
							<th>Email</th>
							<th>Tổng tiền</th>
							<th></th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="${topCustomers}" var="customer">
							<tr>
								<td>${customer.fullname}</td>
								<td>${customer.email}</td>
								<td><fmt:formatNumber value="${customer.totalPurchases}" type="number" pattern="#,###.00" /> VNĐ</td>
								<td><button class="btn btn-success" data-toggle="modal" data-target="#contactModal"
										data-email="${customer.email}" data-phone="${customer.phone}">
									Liên hệ
								</button></td>

							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="panel-footer text-center">
					<img src="/static/images/contact.png">
				</div>
			</div>

			<!-- Modal -->
			<div class="modal fade" id="contactModal" tabindex="-1" role="dialog" aria-labelledby="contactModalLabel" aria-hidden="true">
				<div class="modal-dialog" role="document">
					<div class="modal-content">
						<div class="modal-header bg-success text-white">
							<h2 class="modal-title mx-auto text-center" id="contactModalLabel"><strong>Gửi Email</strong></h2>
							<button type="button" class="close" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<form id="contactForm" action="${pageContext.request.contextPath}/contact-customer" method="post">
							<div class="modal-body">
								<!-- Display customer info -->
								<div class="form-group">
									<label for="customerEmail">Email:</label>
									<input type="email" class="form-control" id="customerEmail" name="email" readonly>
								</div>
								<div class="form-group">
									<label for="customerPhone">Số điện thoại:</label>
									<input type="text" class="form-control" id="customerPhone" readonly>
								</div>
								<div class="form-group">
									<label for="emailSubject">Tiêu đề:</label>
									<input type="text" class="form-control" id="emailSubject" name="subject" required>
								</div>
								<div class="form-group">
									<label for="emailBody">Nội dung:</label>
									<textarea class="form-control" id="emailBody" name="body" rows="5" required></textarea>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
								<button type="submit" class="btn btn-primary">Gửi</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="row">
		<div class="col-md-6">
			<div class="panel panel-warning">
				<div class="panel-heading text-center">
					<h3 class="panel-title">Biểu đồ sản phẩm bán chạy</h3>
				</div>
				<div class="panel-body">
					<div class="chart-container">
						<canvas id="salesChart"></canvas>
					</div>
				</div>
			</div>
		</div>

		<div class="col-md-6">
			<div class="panel panel-info">
				<div class="panel-heading text-center">
					<h3 class="panel-title">Biểu đồ khách hàng</h3>
				</div>
				<div class="panel-body">
					<div class="chart-container">
						<canvas id="customerChart"></canvas>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		// Biểu đồ Doanh số Sản phẩm
		var ctx1 = document.getElementById('salesChart').getContext('2d');
		var salesChart = new Chart(ctx1, {
			type: 'bar',
			data: {
				labels: [
					<c:forEach items="${topSellingProducts}" var="product">
					"${product.name}"<c:if test="${!loop.last}">,</c:if>
					</c:forEach>
				],
				datasets: [{
					label: 'Số lượng bán',
					data: [
						<c:forEach items="${topSellingProducts}" var="product">
						${product.soldQuantity}<c:if test="${!loop.last}">,</c:if>
						</c:forEach>
					],
					backgroundColor: 'rgba(54, 162, 235, 0.2)',
					borderColor: 'rgba(54, 162, 235, 1)',
					borderWidth: 1
				}]
			},
			options: {
				responsive: true,
				scales: {
					y: {
						beginAtZero: true
					}
				}
			}
		});

		// Biểu đồ Mua hàng của Khách hàng
		var ctx2 = document.getElementById('customerChart').getContext('2d');
		var customerChart = new Chart(ctx2, {
			type: 'pie',
			data: {
				labels: [
					<c:forEach items="${topCustomers}" var="customer">
					"${customer.fullname}"<c:if test="${!loop.last}">,</c:if>
					</c:forEach>
				],
				datasets: [{
					label: 'Tổng số đơn hàng',
					data: [
						<c:forEach items="${topCustomers}" var="customer">
						${customer.totalPurchases}<c:if test="${!loop.last}">,</c:if>
						</c:forEach>
					],
					backgroundColor: [
						'rgba(255, 99, 132, 0.2)',
						'rgba(75, 192, 192, 0.2)',
						'rgba(255, 206, 86, 0.2)',
						'rgba(153, 102, 255, 0.2)',
						'rgba(255, 159, 64, 0.2)'
					],
					borderColor: [
						'rgba(255, 99, 132, 1)',
						'rgba(75, 192, 192, 1)',
						'rgba(255, 206, 86, 1)',
						'rgba(153, 102, 255, 1)',
						'rgba(255, 159, 64, 1)'
					],
					borderWidth: 1
				}]
			},
			options: {
				responsive: true
			}
		});

		// Xử lý form cập nhật kho
		$('#updateStockForm').on('submit', function(event) {
			event.preventDefault();
			var formData = $(this).serialize();

			$.ajax({
				url: $(this).attr('action'),
				type: 'POST',
				data: formData,
				success: function(response) {
					alert('Cập nhật thành công!');
					$('#updateModal').modal('hide');
					window.location.reload();
				},
				error: function() {
					alert('Có lỗi xảy ra. Vui lòng thử lại.');
				}
			});
		});

		// Xử lý modal liên hệ
		$('#contactModal').on('show.bs.modal', function (event) {
			var button = $(event.relatedTarget); // Button that triggered the modal
			var email = button.data('email'); // Extract info from data-* attributes
			var phone = button.data('phone');

			var modal = $(this);
			modal.find('#customerEmail').val(email);
			modal.find('#customerPhone').val(phone);
		});
	});

</script>












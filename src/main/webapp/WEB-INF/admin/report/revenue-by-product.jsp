<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglib.jsp" %>

<div class="panel panel-danger">
	<div class="panel-heading">
		<h3 class="panel-title"><strong>DOANH SỐ SẢN PHẨM</strong></h3>
	</div>
	<div class="panel-body">
        <form id="product-form" action="${pageContext.request.contextPath}/admin/revenue-by-product-date" method="get">
			<label for="startDate">Start Date:</label>
			<input type="date" id="startDate" name="startDate" value="${param.startDate}">

			<label for="endDate">End Date:</label>
			<input type="date" id="endDate" name="endDate" value="${param.endDate}">

			<input type="hidden" name="category_id" value="${param.category_id}">

			<button type="submit">Tìm kiếm</button>
		</form>
    </div>
	<div class="panel-footer">
		<form action="/admin/revenue/product">
			<div class="input-group">
				<label class="input-group-addon">Loại sản phẩm: </label>
				<select name="category_id" class="form-control" onchange="this.form.submit()">
					<c:forEach var="c" items="${categories}">
						<option value="${c.id}" ${param.category_id == c.id ? 'selected' : ''}>${c.nameVN}</option>
					</c:forEach>
				</select>
			</div>
		</form>
	</div>
	<table class="table table-hover">
		<thead class="bg-success">
			<tr>
				<th class="text-center">Sản phẩm</th>
				<th class="text-center">Số lượng</th>
				<th class="text-center">Doanh số</th>
				<th class="text-center">Gía thấp nhất</th>
				<th class="text-center">Giá cao nhất</th>
				<th class="text-center">Trung bình</th>
			</tr>
		</thead>
		<tbody class="text-center">
			<c:set var="total" value="0"/>
			<c:forEach var="a" items="${rpProduct}">
				<c:set var="total" value="${total + a[2]}"/>
				<tr>
					<td class="text-left">${a[0]}</td>
					<td><f:formatNumber value="${a[1]}" pattern="#,###"/></td>
					<td><f:formatNumber value="${a[2]}" pattern="#,###.00"/> đ</td>
					<td><f:formatNumber value="${a[3]}" pattern="#,###.00"/> đ</td>
					<td><f:formatNumber value="${a[4]}" pattern="#,###.00"/> đ</td>
					<td><f:formatNumber value="${a[5]}" pattern="#,###.00"/> đ</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<jsp:include page="_revenue-by-product-chart.jsp"/>
	<div class="panel-footer">
		<strong>TỔNG DOANH SỐ: <f:formatNumber value="${total}" pattern="#,###.00"/> đ</strong>
	</div>
</div>

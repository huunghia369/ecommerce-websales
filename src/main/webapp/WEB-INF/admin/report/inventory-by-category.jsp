<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglib.jsp" %>

<div class="panel panel-danger">
	<div class="panel-heading">
		<h3 class="panel-title"><strong>HÀNG TỒN KHO THEO LOẠI</strong></h3>
	</div>
	<div class="panel-body">
        <form id="iventory-form" action="${pageContext.request.contextPath}/admin/inventory-by-category-date" method="get">
			<label for="startDate">Start Date:</label>
			<input type="date" id="startDate" name="startDate" value="${param.startDate}">

			<label for="endDate">End Date:</label>
			<input type="date" id="endDate" name="endDate" value="${param.endDate}">

			<button type="submit">Tìm kiếm</button>
		</form>
    </div>
	<table class="table table-hover">
		<thead class="bg-success">
			<tr>
				<th class="text-center">Loại sản phẩm</th>
				<th class="text-center">Tổng số lượng</th>
				<th class="text-center">Giá trị</th>
				<th class="text-center">Giá thấp nhất</th>
				<th class="text-center">Giá cao nhất</th>
				<th class="text-center">Trung bình</th>
			</tr>
		</thead>
		<tbody class="text-center">
		<c:set var="total" value="0"/>
		<c:forEach var="a" items="${data}">
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
	<jsp:include page="_inventory-chart.jsp"></jsp:include>



	<div class="panel-footer">
		<strong>TỔNG DOANH THU : <f:formatNumber value="${total}" pattern="#,###.00"/> đ</strong>
	</div>
</div>
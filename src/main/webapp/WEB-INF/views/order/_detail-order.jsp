<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglib.jsp"%>
<div class="panel-footer">
	<h4 class="panel-title">CHI TIẾT ĐƠN HÀNG</h4>
</div>
<table class="table table-hover">
	<thead>
		<tr>
			<th>Id</th>
			<th>Name</th>
			<th>Unit Price</th>
			<th>Discount</th>
			<th>Quantity</th>
			<th>Amount</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="d" items="${order.orderDetails}">
			<tr>
				<td>${d.product.id}</td>
				<td>${d.product.name}</td>
				<td><f:formatNumber value="${d.unitPrice}" pattern="#,###.00" /> đ</td>
				<td><f:formatNumber value="${d.discount * 100}"
						type="percent" /></td>
				<td>${d.quantity}</td>
				<td><f:formatNumber
						value="${d.unitPrice*d.quantity*(1-d.discount)}"
						pattern="#,###.00" /> đ</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<div class="panel-body text-center">
	<img src="/static/images/stamp.png" style="width: 100px"> <br>
	<img src="/static/images/signature.png" style="width: 200px">
</div>

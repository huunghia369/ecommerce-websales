<%@ page pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<style id="cart-effect"></style>
<div class="panel panel-danger nn-cart">
	<div class="panel-heading">
		<h3 class="panel-title">
			<span class="glyphicon glyphicon-shopping-cart"></span>Giỏ hàng
		</h3>
	</div>
	<div class="panel-body">
		<div class="col-xs-5">
			<img id="cart-img" src="/static/images/shopping-cart.jpg">
		</div>
		<ul class="col-xs-7 text-right">
		<!-- truy xuất tới thuộc tính trong bean <==> countCart = getCountCart -->
 			<li><strong id="cart-cnt">${cart.countCart}</strong> món hàng</li>
			<li><strong id="cart-amt">${fn:escapeXml(Math.round(cart.amountCart))}</strong> đ</li>
			<li><a href="/cart/view">Xem chi tiết</a></li>
		</ul> 
	</div>
</div>

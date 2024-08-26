$(function() {
	// thêm giỏ hàng
	$(".btn-add-to-cart").click(function() {
		// lay id sp
		var id = $(this).parents("[data-id]").attr("data-id");
		$.ajax({
			url : "/product/add-to-cart/" + id,
			success: function(res) {
				// nhận chuỗi vừa trả về từ server
				var info = JSON.parse(res);
				// kiểm tra xem có trường error hay không
				if (info.error) {
					alert(info.error); // hiển thị thông báo lỗi
				} else {
					updateCartInfo(info); // cập nhật thông tin giỏ hàng
					alert("Sản phẩm đã được thêm vào giỏ hàng!");
				}
			},
			error: function(xhr) {
				alert("Đã xảy ra lỗi khi thêm sản phẩm vào giỏ hàng.");
			}
		});
		effectCart(id);
	});


	// xóa từng item
	$(".btn-cart-remove").click(function() {
		// lay id sp
		var id = $(this).parents("[data-id]").attr("data-id");
		$.ajax({
			url : "/cart/remove/" + id,
			success : function(res) {
				// nhận chuỗi vừa trả về từ sv (count + amount)-> biến thành đối
				// tượng json
				var info = JSON.parse(res);
				// bỏ giữa vào 2 thẻ
				updateCartInfo(info);
			}
		});
	// hiệu ứng ẩn dòng sản phẩm khi xóa
		$(this).parents("[data-id]").hide(100);
	});

	// chức năng xóa tất cả giỏ hàng
	$(".btn-cart-clear").click(function(){
		$.ajax({
			url: "/cart/clear",
			success : function(res)
			{
				var info = JSON.parse(res);
				updateCartInfo(info);
			}
		});
/*
 * ẩn tất cả <tr> bằng cách ẩn <tbody> $(".hide-cart").hide(100);
 */

		// hiệu ứng ẩn duyệt qua từng thẻ <tr>
		$(".hide-cart").each(function(index,tr){
			$(tr).hide(500 * (1 + index));
		})
	});

	$(".txt-cart-quantity").on("input", function() {
		var id = $(this).parents("[data-id]").attr("data-id");
		var qty = $(this).val();
		if (qty == 0) {
			$.ajax({
				url: `/cart/remove/${id}`,
				success: function(res) {
					var info = JSON.parse(res);
					updateCartInfo(info);
					$(`[data-id=${id}]`).hide(100); // Ẩn sản phẩm đã xóa
				},
				error: function(xhr) {
					alert("Đã xảy ra lỗi khi xóa sản phẩm.");
				}
			});
			return; // Ngăn chặn thực hiện tiếp các đoạn mã phía dưới
		}
		$.ajax({
			url: `/cart/update/${id}/${qty}`,
			success: function(res) {
				var info = JSON.parse(res);
				if (info.error) {
					alert(info.error);
				} else {
					updateCartInfo(info);
				}
			},
			error: function(xhr) {
				alert("Số lượng sản phẩm không hợp lệ.");
			}
		});

		var price = $(this).parents("[data-price]").attr("data-price");
		var disc = $(this).parents("[data-discount]").attr("data-discount");
		var amount = Math.round(price * qty * (1 - disc) * 100) / 100;
		$(this).parents("tr").find("td:eq(5)").html(`${amount} đ`);
	});


})
function updateCartInfo(info) {
	$("#cart-cnt").html(info.count);
	$("#cart-amt").html(info.amount);
}
function effectCart(id) {
	var img = $(`[data-id=${id}] .panel-body img`);

	if (img.length === 0) {
		console.warn("No image found for the specified ID.");
		return;
	}

	$("style#cart-effect").html(`.cart-effect { background-image:
        url("${img.attr("src")}"); background-size: 100% 100%; }`);

	var options = {
		to: "#cart-img",
		className: "cart-fly"
	};

	// Kiểm tra xem phương thức effect có tồn tại
	if ($.isFunction(img.effect)) {
		img.effect("transfer", options, 1000);
	} else {
		console.warn("jQuery UI effect not found or not loaded properly.");
	}
}


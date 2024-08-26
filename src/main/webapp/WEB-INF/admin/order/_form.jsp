<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglib.jsp" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<form:form modelAttribute="order" action="${prefix}/index">
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="row">
				<div class="form-group col-sm-4">
					<label>Mã</label>
					<form:input path="id" class="form-control" readonly="true"
						placeholder="Auto Number" />
				</div>
				<div class="form-group col-sm-4">
					<label>Khách hàng</label>
					<form:input path="customer.fullname" required="required" class="form-control" />
				</div>
				<div class="form-group col-sm-4">
					<label>SĐT đặt hàng</label>
					<form:input path="customer.phone" class="form-control"
									required="required"
									pattern="([0-9]{10})\b" />
				</div>
			</div>
			<div class="row">
				<div class="form-group col-sm-4">
					<label>Ngày đặt</label>
					<form:input path="orderDate" class="form-control datepicker" />
				</div>
				<div class="form-group col-sm-4">
					<label>Tổng cộng</label>
					<form:input path="amount" class="form-control" />
				</div>
				<div class="form-group col-sm-4">
					<label>Địa chỉ vận chuyển</label>
					<form:input path="address" class="form-control" />
				</div>
			</div>
			<div class="row">
				<div class="form-group col-sm-12">
					<label>Mô tả</label>
					<form:textarea path="description" rows="3" class="form-control" />
				</div>
				<div class="form-group col-sm-12">
					<label>Chi tiết đơn hàng</label>
					<jsp:include page="_details.jsp" />
				</div>
			</div>
		</div>
		<div class="form-group col-sm-12"><label> Trạng thái đơn hàng</label></div>
		<div class="panel-footer text-right">
			<div class="row">
				<div class="col-sm-6 text-left">
					<div class="col-sm-3">
						<label> <input type="checkbox" name="status" value="2" id="xacNhanStatus" /> Xác
							nhận
						</label>
					</div>
					<div class="col-sm-3">
						<label> <input type="checkbox" name="status" value="3" id="dangGiaoStatus"/> Đã giao
						</label>
					</div>
					<div class="col-sm-3">
						<label> <input type="checkbox" name="status" value="0" id="hoanTatStatus" /> Hoàn tất
						</label>
					</div>
					<div class="col-sm-3">
						<label> <input type="checkbox" name="status" value="4" id="daHuyStatus" /> Đã hủy
						</label>
					</div>
				</div>
				<div class="col-sm-6 text-right">
					<jsp:include page="../layout/_btn-crud.jsp" />
				</div>
			</div>
			<div class="row" style="text-align: center;">
				<div class="text-danger">${message}${param.message}</div>
				<!-- Thêm liên kết tải hóa đơn khi trạng thái là "Hoàn tất" -->
				<c:if test="${order.status == 0}">
					<a href="${pageContext.request.contextPath}/admin/order/invoice/${order.id}"
					   class="btn btn-primary">Tải Hóa Đơn</a>
				</c:if>
			</div>
		</div>
	</div>
</form:form>
<script>
	$(function() {
		bkLib.onDomLoaded(nicEditors.allTextAreas);

		$(window).resize(function() {
			var nicedit = $("textarea").parent().find(">div");
			nicedit.css({"width": "100%"});
			nicedit.find("[contenteditable]").width(nicedit.width() - 8);
			nicedit.find("[contenteditable]").css({"outline": "none"});
		});
		$(window).resize();

		$("input:checkbox").on('click', function() {
			var $box = $(this);
			if ($box.is(":checked")) {
				var group = "input:checkbox[name='" + $box.attr("name") + "']";
				$(group).prop("checked", false);
				$box.prop("checked", true);
			} else {
				$box.prop("checked", false);
			}
		});

		let vStatus = ${order.status};
		if (vStatus == 1) {
			$("#dangGiaoStatus").attr("disabled", true);
			$("#hoanTatStatus").attr("disabled", true);
		} else if (vStatus == 2) {
			$("#xacNhanStatus").attr("disabled", true);
			$("#xacNhanStatus").attr("checked", true);
			$("#hoanTatStatus").attr("disabled", true);
			$("#daHuyStatus").attr("disabled", true);
		} else if (vStatus == 3) {
			$("#xacNhanStatus").attr("disabled", true);
			$("#xacNhanStatus").attr("checked", true);
			$("#dangGiaoStatus").attr("disabled", true);
			$("#dangGiaoStatus").attr("checked", true);
			$("#daHuyStatus").attr("disabled", true);
		} else if (vStatus == 4) {
			$("#xacNhanStatus").attr("disabled", true);
			$("#dangGiaoStatus").attr("disabled", true);
			$("#hoanTatStatus").attr("disabled", true);
			$("#daHuyStatus").attr("checked", true);
		} else if (vStatus == 0) {
			$("#xacNhanStatus").attr("disabled", true);
			$("#xacNhanStatus").attr("checked", true);
			$("#dangGiaoStatus").attr("disabled", true);
			$("#dangGiaoStatus").attr("checked", true);
			$("#hoanTatStatus").attr("disabled", true);
			$("#hoanTatStatus").attr("checked", true);
			$("#daHuyStatus").attr("disabled", true);
		}

		$(".datepicker").datepicker({
			dateFormat: 'mm/dd/yy',
			minDate: 0,
		});
	});
</script>
<script>
    $(document).ready(function() {
        var printInvoice = ${printInvoice ? 'true' : 'false'};
        var orderId = ${orderId};

        if (printInvoice && orderId) {
            if (confirm("Đơn hàng đã được cập nhật với trạng thái 'Hoàn tất'. Bạn có muốn in hóa đơn không?")) {
                window.open("${pageContext.request.contextPath}/admin/order/invoice/" + orderId);
            }
        }
    });
</script>
<%@ page pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<button formaction="${prefix}/create" ${empty form.id ? '' : 'disabled'} class="btn btn-primary" onclick="return confirmAction('Are you sure you want to create this item?', event)">
	<span class="glyphicon glyphicon-plus"></span> Thêm
</button>
<button formaction="${prefix}/update" ${empty form.id ? 'disabled' : ''} class="btn btn-success" onclick="return confirmAction('Are you sure you want to update this item?', event)">
	<span class="glyphicon glyphicon-save"></span> Cập nhật
</button>
<button formaction="${prefix}/delete/${form.id}" ${empty form.id ? 'disabled' : ''} class="btn btn-danger" onclick="return confirmAction('Are you sure you want to delete this item?', event)">
	<span class="glyphicon glyphicon-trash"></span> Xóa
</button>
<a href="${prefix}/index" class="btn btn-info">
	<span class="glyphicon glyphicon-refresh"></span> Reset
</a>

<script>
	function confirmAction(message, event) {
		// Hiển thị hộp thoại xác nhận với thông điệp truyền vào
		const confirmed = confirm(message);
		if (!confirmed) {
			// Ngăn chặn hành động mặc định của nút nếu người dùng không xác nhận
			event.preventDefault();
		}
		return confirmed;
	}
</script>

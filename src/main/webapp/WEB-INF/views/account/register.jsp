<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglib.jsp"%>
<style>
/* CSS cho form đăng ký */
.register-form {
    max-width: 500px;
    margin: auto;
    padding: 20px;
}

.card {
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    position: relative; /* Để có thể tạo viền ngoài thêm */
}

.card::before {
    content: ""; /* Tạo một viền ngoài bằng pseudo-element */
    position: absolute;
    top: -5px;
    left: -5px;
    right: -5px;
    bottom: -5px;
    border-radius: 12px; /* Bo góc viền ngoài để đồng bộ với card */
}

.card-header {
    background-color: #28a745; /* Màu nền xanh */
    color: #fff; /* Màu chữ trắng */
    padding: 15px;
    border-radius: 10px 10px 0 0;
    text-align: center; /* Canh giữa tiêu đề */
}

.card-title {
    margin: 0;
    font-size: 1.5rem; /* Kích thước chữ lớn hơn cho tiêu đề */
}

.card-body {
    padding: 20px;
}

.form-group label {
    font-weight: bold;
}

.form-control {
    border-radius: 5px;
    border: 1px solid #ccc;
    padding: 10px;
    margin-bottom: 15px;
}

.form-control::placeholder {
    font-style: italic;
}

.btn-primary {
    background-color: #28a745; /* Màu nền xanh */
    border: none;
    color: #fff;
    padding: 10px 20px;
	margin-bottom: 15px;
	border-radius: 5px;
    font-size: 1rem;
    cursor: pointer;
}

.btn-primary:hover {
    background-color: #218838; /* Màu nền khi hover */
}

.text-danger {
    color: #dc3545;
}
</style>

<div class="register-form">
    <div class="card">
        <div class="card-header">
            <h4 class="card-title">ĐĂNG KÍ</h4>
        </div>
        <div class="card-body">
            <form:form action="/account/register" modelAttribute="user" enctype="multipart/form-data">
                <div class="form-group">
                    <label for="id">Tài khoản</label>
                    <form:input path="id" id="id" class="form-control" placeholder="Vui lòng nhập tên tài khoản" required="required"/>
                    <form:errors path="id" cssClass="text-danger"></form:errors>
                </div>
                <div class="form-group">
                    <label for="password">Mật khẩu</label>
                    <form:input path="password" id="password" class="form-control" placeholder="Vui lòng nhập mật khẩu" type="password" required="required"/>
                    <form:errors path="password" cssClass="text-danger"></form:errors>
                </div>
                <div class="form-group">
                    <label for="confirm">Xác nhận mật khẩu</label>
                    <input name="confirm" id="confirm" type="password" value="${param.confirm}" class="form-control" placeholder="Vui lòng nhập lại mật khẩu" required="required"/>
                </div>
                <div class="form-group">
                    <label for="fullname">Họ Tên</label>
                    <form:input path="fullname" id="fullname" class="form-control" placeholder="Vui lòng nhập họ tên" required="required"/>
                    <form:errors path="fullname" cssClass="text-danger"></form:errors>
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <form:input path="email" id="email" class="form-control" placeholder="Vui lòng nhập địa chỉ email" type="email" required="required"/>
                    <form:errors path="email" cssClass="text-danger"></form:errors>
                </div>
                <div class="form-group">
                    <label for="phone">SĐT</label>
                    <form:input path="phone" id="phone" class="form-control" placeholder="Vui lòng nhập số điện thoại" required="required" pattern="([0-9]{10})\b"/>
                    <form:errors path="phone" cssClass="text-danger"></form:errors>
                </div>
                <div class="form-group">
                    <label for="photo_file">Hình đại diện</label>
                    <input type="file" name="photo_file" id="photo_file" class="form-control" />
                    <form:hidden path="photo" />
                </div>
                <div class="form-group text-right">
                    <div class="pull-left text-danger">${message}</div>
                    <button type="submit" class="btn btn-primary">
                        <span class="glyphicon glyphicon-user"></span> Đăng kí
                    </button>
                </div>
            </form:form>
        </div>
    </div>
</div>

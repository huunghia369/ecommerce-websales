<%@ page pageEncoding="utf-8"%>
<form action="/account/login" method="post" class="login-form">
	<div class="card">
		<div class="card-header">
			<h4 class="card-title">ĐĂNG NHẬP</h4>
		</div>
		<div class="card-body">
			<div class="form-group">
				<label for="username">Tài khoản</label>
				<input id="username" name="username" value="${username}" class="form-control" required>
			</div>
			<div class="form-group">
				<label for="password">Mật khẩu</label>
				<input id="password" type="password" name="password" value="${password}" class="form-control" required>
			</div>
			<div class="form-group form-check">
				<input id="remember" name="remember" type="checkbox" value="true" ${empty username ? '' : 'checked'} class="form-check-input">
				<label for="remember" class="form-check-label">Ghi nhớ tài khoản?</label>
			</div>
		</div>
		<div class="card-footer text-right">
			<div class="text-danger">${message}${param.message}</div>
			<button type="submit" class="btn btn-primary">
				<span class="glyphicon glyphicon-user"></span> Đăng nhập
			</button>
		</div>
	</div>
</form>
<style>
	/* Toàn bộ giao diện của form */
	.login-form {
		max-width: 500px;
		margin: auto;
		padding: 20px;
	}

	/* Cập nhật styling của card để thêm viền ngoài */
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
		z-index: -1; /* Đưa pseudo-element ra sau card */
	}

	.card-header {
		background-color: #007bff;
		color: #fff;
		padding: 15px;
		border-radius: 10px 10px 0 0;
	}

	.card-title {
		margin: 0;
		font-size: 1.25rem;
		text-align: center;
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

	.form-check {
		margin-bottom: 15px;
	}

	.form-check-input {
		margin-right: 10px;
	}

	.btn-primary {
		background-color: #007bff;
		border: none;
		color: #fff;
		padding: 10px 20px;
		margin-right: 10px;
		margin-bottom: 15px;
		border-radius: 5px;
		font-size: 1rem;
		cursor: pointer;
	}

	.btn-primary:hover {
		background-color: #0056b3;
	}

	.text-danger {
		color: #dc3545;
		margin-right: 15px;
		margin-bottom: 20px;
	}
</style>
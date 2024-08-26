<%@ page pageEncoding="utf-8"%>
<style>
	/* CSS cho form quên mật khẩu */
	.forgot-password-form {
		max-width: 500px;
		margin: auto;
		padding: 20px;
	}

	.card {
		border-radius: 10px;
		box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
		position: relative;
	}

	.card::before {
		content: "";
		position: absolute;
		top: -5px;
		left: -5px;
		right: -5px;
		bottom: -5px;
		border-radius: 12px;
	}

	.card-header {
		background-color: #dc3545; /* Màu nền đỏ */
		color: #fff; /* Màu chữ trắng */
		padding: 15px;
		border-radius: 10px 10px 0 0;
		text-align: center;
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

	.btn-danger {
		background-color: #dc3545; /* Màu nền đỏ */
		border: none;
		color: #fff;
		padding: 10px 20px;
		border-radius: 5px;
		font-size: 1rem;
		cursor: pointer;
	}

	.btn-danger:hover {
		background-color: #c82333; /* Màu nền khi hover */
	}

	.text-danger {
		color: #dc3545;
	}
</style>

<div class="forgot-password-form">
	<div class="card">
		<div class="card-header">
			<h4 class="card-title">Quên mật khẩu</h4>
		</div>
		<div class="card-body">
			<form action="/account/forgot" method="post">
				<div class="form-group">
					<label for="username">Tài khoản</label>
					<input name="username" id="username" class="form-control" placeholder="Vui lòng nhập tên tài khoản" required="required">
				</div>
				<div class="form-group">
					<label for="email">Địa chỉ email</label>
					<input name="email" id="email" class="form-control" placeholder="Vui lòng nhập địa chỉ email" type="email" required="required">
				</div>
				<div class="form-group text-danger">${message}</div>
				<div class="form-group text-right">
					<button type="submit" class="btn btn-danger">
						<span class="glyphicon glyphicon-envelope"></span> Retrieve
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

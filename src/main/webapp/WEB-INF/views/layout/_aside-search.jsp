<%@ page pageEncoding="utf-8"%>

<div class="panel panel-info">
	<div class="panel-heading">
		<h3 class="panel-title">
			<span class="glyphicon glyphicon-search"></span> Tìm kiếm
		</h3>
	</div>
	<div class="panel-body">
		<form action="/product/list-by-keywords" method="get">
			<div class="input-group">
				<input name="keywords" placeholder="Nhập thông tin cần tìm" class="form-control">
				<span class="input-group-btn">
                    <button class="btn btn-default" type="submit">
                        <span class="glyphicon glyphicon-search"></span>
                    </button>
                </span>
			</div>
		</form>
	</div>
</div>
<%@ page pageEncoding="utf-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<form:form modelAttribute="category" action="">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h4 class="panel-title"> THAO TÁC LOẠI SẢN PHẨM</h4>
		</div>

		<div class="panel-body">
			<div class="form-group">
				<label>Id</label>
				<form:input path="id" class="form-control" readonly="true"
					placeholder="Auto Number" />
			</div>
			<div class="form-group">
				<label>Name</label>
				<form:errors path="name" />
				<form:input path="name" class="form-control" placeholder="English Name"/>
			</div>
			<div class="form-group">
				<label>Name (VN)</label>
				<form:errors path="nameVN" />
				<form:input path="nameVN" class="form-control" placeholder="Vietnamese Name"/>
			</div>
		</div>
		<div class="panel-footer text-right">
			<div class="pull-left text-danger">${message}${param.message}</div>
			<jsp:include page="_btn-crud.jsp" />
		</div>
	</div>
</form:form>
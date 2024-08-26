<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglib.jsp"%>

<style>
    .modal-content-success {
        background-color: #5cb85c;
        color: white;
        border-radius: 10px;
        text-align: center;
        padding: 20px;
    }
    .modal-content-danger {
        background-color: #d9534f;
        color: white;
        border-radius: 10px;
        text-align: center;
        padding: 20px;
    }
    .modal-icon {
        font-size: 50px;
        margin-bottom: 20px;
    }
    .modal-icon-success {
        color: white;
    }
    .modal-icon-danger {
        color: white;
    }
</style>

<!-- Modal -->
<div id="notificationModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="notificationModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <!-- Success Message -->
            <c:if test="${not empty message}">
                <div class="modal-content-success">
                    <span class="glyphicon glyphicon-ok-circle modal-icon modal-icon-success" aria-hidden="true"></span>
                    <h2>Thành công!</h2><br>
                    <p>${message}</p>
                </div>
            </c:if>
            <!-- Error Message -->
            <c:if test="${not empty error}">
                <div class="modal-content-danger">
                    <span class="glyphicon glyphicon-remove-circle modal-icon modal-icon-danger" aria-hidden="true"></span>
                    <h2>Lỗi!</h2><br>
                    <p>${error}</p>
                </div>
            </c:if>
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        <% if ((request.getAttribute("message") != null) || (request.getAttribute("error") != null)) { %>
            $('#notificationModal').modal('show');
        <% } %>
    });
</script>

<!-- mapping tới controller , ko xài jsp -->
<c:import url="/home/slideshow"></c:import>
<hr>
<c:import url="/home/random"></c:import>

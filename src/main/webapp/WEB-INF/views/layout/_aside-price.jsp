<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="panel panel-warning">
    <div class="panel-heading">
        <h3 class="panel-title">
            <span class="glyphicon glyphicon-tag"></span> Giá bán
        </h3>
    </div>
    <div class="panel-body">
        <!-- Form tìm kiếm theo khoảng giá -->
        <form action="${pageContext.request.contextPath}/product/search-by-price-range" method="get" class="bg-light p-4 rounded shadow-sm">
            <div class="form-row">
                <div class="form-group col-md-6">
                    <label for="minPrice">Giá tối thiểu</label>
                    <input type="number" id="minPrice" name="minPrice" class="form-control" placeholder="Nhập giá tối thiểu" step="10" min="0" required>
                </div>
                <div class="form-group col-md-6">
                    <label for="maxPrice">Giá tối đa</label>
                    <input type="number" id="maxPrice" name="maxPrice" class="form-control" placeholder="Nhập giá tối đa" step="10" min="0" required>
                </div>
            </div>
            <button type="submit" class="btn btn-warning btn-block">Lọc</button>
        </form>
        <!-- Form tìm kiếm theo khoảng giá đã định sẵn -->
        <form action="${pageContext.request.contextPath}/product/search-by-predefined-price-range" method="get" class="mb-4"><br>
            <div class="form-group">
                <select id="predefinedPriceRange" name="priceRange" class="form-control" required>
                    <option value="">-- Chọn khoảng giá --</option>
                    <option value="0-10">Dưới 10₫</option>
                    <option value="10-50">10₫ - 50₫</option>
                    <option value="50-100">50₫ - 100₫</option>
                    <option value="100-200">100₫ - 200₫</option>
                    <option value="200-500">200₫ - 500₫</option>
                    <option value="500-10000">500₫ - 1,000₫</option>
                    <option value="1000-">Trên 1,000₫</option>
                </select>
            </div>
        </form>
    </div>
</div>
<%--<div class="vertical-panel">--%>
<%--	<div class="slider">--%>
<%--		<div><img src="https://d1csarkz8obe9u.cloudfront.net/posterpreviews/big-sale-with-beige-minimalist-background-design-template-223b5120ca256caf0e894fcf379184d6_screen.jpg?ts=1698399309" alt="Hình ảnh sản phẩm 1"></div>--%>
<%--		<div><img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSm_Q78F6_7Sv9X2wgLfH8Kwx8A9N2QeOlIWQ&s" alt="Hình ảnh sản phẩm 2"></div>--%>
<%--		<div><img src="https://d1csarkz8obe9u.cloudfront.net/posterpreviews/super-big-sale-instagram-story-design-template-e3fcb3c6a4507079c75394bb03a86790_screen.jpg?ts=1697873520" alt="Hình ảnh sản phẩm 3"></div>--%>
<%--		<!-- Thêm nhiều hình ảnh ở đây -->--%>
<%--	</div>--%>
<%--</div>--%>
<script>
    function submitFormOnSelectChange() {
        var selectElement = document.getElementById('predefinedPriceRange');
        selectElement.addEventListener('change', function() {
            this.form.submit();
        });
    }

    window.onload = submitFormOnSelectChange;

    // $('.slider').slick({
    // 	infinite: true,
    // 	slidesToShow: 1,
    // 	slidesToScroll: 1,
    // 	autoplay: true,
    // 	autoplaySpeed: 3000, // Thay đổi mỗi 3 giây
    // 	arrows: false, // Ẩn các mũi tên điều hướng
    // 	dots: true // Hiển thị các điểm điều hướng dưới slider
    // });
</script>

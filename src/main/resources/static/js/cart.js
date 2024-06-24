$(document).ready(function () {
    // Xử lý sự kiện khi nhấn nút giảm số lượng
    $('.btn-num-product-down').click(function (e) {
        e.preventDefault();
        var foodId = $(this).data('food-id');
        decreaseCart(foodId, 1); // Gọi hàm decreaseCart với quantityChange = -1
    });

    // Xử lý sự kiện khi nhấn nút tăng số lượng
    $('.btn-num-product-up').click(function (e) {
        e.preventDefault();
        var foodId = $(this).data('food-id');
        increaseCart(foodId, 1); // Gọi hàm increaseCart với quantityChange = 1
    });

    // Hàm gửi AJAX request để cập nhật giỏ hàng khi tăng số lượng
    function increaseCart(foodId, quantityChange) {
        var currentQuantity = parseInt($('.num-product[data-food-id="' + foodId + '"]').val());
        var newQuantity = currentQuantity + quantityChange;

        // Nếu số lượng mới nhỏ hơn 1, không cho phép giảm thêm
        if (newQuantity < 1) {
            return;
        }

        // Gửi AJAX request
        $.ajax({
            type: 'POST',
            url: '/cart/increase/' + foodId + '/' + newQuantity,
            success: function (data) {
                // Cập nhật số lượng hiển thị trên giao diện
                $('.num-product[data-food-id="' + foodId + '"]').val(newQuantity);
                // Cập nhật tổng tiền sản phẩm
                var totalPrice = data.price * newQuantity;
                $('.column-5[data-food-id="' + foodId + '"]').text('$ ' + totalPrice.toFixed(2));
            },
            error: function (xhr, status, error) {
                console.error('Error updating cart:', error);
            }
        });
    }

    // Hàm gửi AJAX request để cập nhật giỏ hàng khi giảm số lượng
    function decreaseCart(foodId, quantityChange) {
        var currentQuantity = parseInt($('.num-product[data-food-id="' + foodId + '"]').val());
        var newQuantity = currentQuantity + quantityChange;

        // Nếu số lượng mới nhỏ hơn 1, không cho phép giảm thêm
        if (newQuantity < 1) {
            return;
        }

        // Gửi AJAX request
        $.ajax({
            type: 'POST',
            url: '/cart/decrease/' + foodId + '/' + newQuantity,
            success: function (data) {
                // Cập nhật số lượng hiển thị trên giao diện
                $('.num-product[data-food-id="' + foodId + '"]').val(newQuantity);
                // Cập nhật tổng tiền sản phẩm
                var totalPrice = data.price * newQuantity;
                $('.column-5[data-food-id="' + foodId + '"]').text('$ ' + totalPrice.toFixed(2));
            },
            error: function (xhr, status, error) {
                console.error('Error updating cart:', error);
            }
        });
    }
});

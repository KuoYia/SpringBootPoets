

    // 确保jQuery库已经加载完成再绑定事件
    function formatDate(date) {
    if (!date) return '';
    var d = new Date(date);
    return d.toISOString().split('T')[0]; // 格式化为 yyyy-MM-dd
}

    function addPoet() {
    var poetData = {
    name: $('#name').val(),
    birthDate: $('#birthDate').val(), // 直接使用输入的日期值
    deathDate: $('#deathDate').val(), // 直接使用输入的日期值
    dynasty: $('#dynasty').val(),
    biography: $('#biography').val()
};

    // 确保输入的日期格式正确，如果不正确则需要进行格式化
    if (poetData.birthDate) {
    poetData.birthDate = formatDate(poetData.birthDate);
}
    if (poetData.deathDate) {
    poetData.deathDate = formatDate(poetData.deathDate);
}

    $.ajax({
    url: '/api/poets/add',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(poetData),
    success: function(response) {
    alert('诗人添加成功');
},
    error: function(error) {
    alert('添加诗人时发生错误: ' + error.responseText);
}
});
}

    function updatePoet() {
    // 构建要发送的数据对象
    var poetData = {
    id: $('#updateId').val(),
    name: $('#updateName').val(),
    dynasty: $('#updateDynasty').val(),
    biography: $('#updateBiography').val(),
    birthDate: $('#updateBirthDate').val() ? formatDate(new Date($('#updateBirthDate').val())) : '', // 如果生年不为空，则格式化生年
    deathDate: $('#updateDeathDate').val() ? formatDate(new Date($('#updateDeathDate').val())) : ''  // 如果卒年不为空，则格式化卒年
};

    // 发送 AJAX 请求
    $.ajax({
    url: '/api/poets/' + $('#updateId').val(), // 修改URL
    type: 'PUT', // 修改请求类型
    contentType: 'application/json',
    data: JSON.stringify(poetData),
    success: function(response) {
    alert('诗人更新成功');
},
    error: function(error) {
    alert('更新诗人时发生错误: ' + error.responseText);
}
});
}

    function deletePoet() {
        var poetId = $('#deleteId').val();
        $.ajax({
            url: '/api/poets/' + poetId,
            type: 'DELETE',
            success: function(response) {
                if (response === 1) {
                    alert('诗人删除成功');
                    // 可以在这里添加代码来更新页面，例如移除诗人的列表项
                } else {
                    // 显示后端返回的具体错误消息
                    alert(response);
                }
            },
            error: function(error) {
                // 显示后端返回的通用错误消息
                alert('删除诗人时发生错误：' + error.responseJSON);
            }
        });
    }

    /**
     * 根据ID获取诗人信息并更新表格
     * 使用AJAX发送GET请求到服务器，获取诗人信息后在表格中显示
     * @function getPoetById
     */
    function getPoetById() {
        // 发送AJAX GET请求到服务器，URL根据输入的ID动态生成
        $.ajax({
            url: '/api/poets/' + $('#getId').val(), // 修改URL
            type: 'GET', // 修改请求类型
            success: function(response) {
                // 请求成功后，将诗人信息添加到表格中
                $('#poetTable').append('<tr><td>' + response.id + '</td><td>' + response.name + '</td><td>' + response.dynasty + '</td><td>' + response.biography + '</td></tr>');
            },
            error: function(error) {
                // 请求失败时，弹出错误提示
                alert('获取诗人时发生错误: ' + error.responseText);
            }
        });




}

function download() {
    $('#img').attr('src', $('#input').val())
}

$('#img').click(function (e) {
    var realSize = dimension($(this).attr('src'));
    var scaleX = 1.0 * realSize.width / $(this).width();
    var scaleY = 1.0 * realSize.height / $(this).height();
    var x = Math.round(e.offsetX * scaleX);
    var y = Math.round(e.offsetY * scaleY);
    window.prompt("Copy to clipboard: Ctrl+C, Enter",  x + ',' + y);
});

function dimension(link) {
    var img = new Image();
    img.src = link;
    return {
        width: img.width,
        height: img.height
    };
}
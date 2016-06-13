<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">

    <title>Разметка картинок</title>

<#setting locale="en_US">
<#setting number_format="0.######">

    <style type="text/css">
        body {
            background: #d8f1f3 !important;
        }
    </style>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
</head>
<body>

<h3 align="center">Координаты пикселя на картинке</h3>

<div class="form-group">
    <div class="form-group col-lg-5">
        <div class="input-group">
            <span class="input-group-addon">Ссылка на картинку</span>
            <input id="link" type="url" class="form-control" <#if link??>value="${link}"</#if>>
        </div>
    </div>
    <div class="form-group col-lg-5">
        <div class="input-group">
            <span class="input-group-addon">Координаты пикселя</span>
            <input id="coords" type="text" class="form-control" <#if point??>value="${point.x},${point.y}"</#if>>
        </div>
    </div>
    <div class="form-group col-lg-2">
        <a class="btn btn-primary" onclick="drawPixel()">Показать!</a>
    </div>
</div>

<canvas id="image"/>

<script type="text/javascript">
    var image;
    var canvas;

    <#if link??>
    drawPixel();
    </#if>

    $('#image').mousedown(function (e) {
        if (e.which != 1 && e.which != 3) {
            return;
        }
        var coords = canvasMouseCoords(e);
        var x = Math.floor(coords.x * image.width / canvas.width);
        var y = Math.floor(coords.y * image.height / canvas.height);
        window.prompt("Скопировать в буфер обмена: Ctrl+C, Enter", x + ',' + y);
    });

    function canvasMouseCoords(e) {
        var x;
        var y;
        if (e.pageX || e.pageY) {
            x = e.pageX;
            y = e.pageY;
        } else {
            x = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
            y = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
        }
        x -= canvas.offsetLeft;
        y -= canvas.offsetTop;
        return {x: x, y: y};
    }

    function drawPixel() {
        var $image = document.getElementById('image');
        var ctx = $image.getContext('2d');
        image = new Image();
        image.onload = function () {
            canvas = ctx.canvas;
            canvas.width = window.innerWidth;
            canvas.height = image.height * canvas.width / image.width;
            ctx.drawImage(image, 0, 0, image.width, image.height, 0, 0, canvas.width, canvas.height);
            var coords = $('#coords').val();
            if (!coords) {
                return;
            }
            var x = parseInt(coords.split(',')[0]) * canvas.width / image.width;
            var y = parseInt(coords.split(',')[1]) * canvas.height / image.height;
            drawLocation(ctx, x, y);
        };
        image.src = $('#link').val();
    }

    function drawLocation(ctx, x, y) {
        ctx.beginPath();
        ctx.arc(x, y, 4, 0, Math.PI * 2, true);
        ctx.closePath();
        ctx.fillStyle = 'red';
        ctx.fill();

        ctx.beginPath();
        ctx.arc(x, y, 20, 0, Math.PI * 2, true);
        ctx.closePath();
        ctx.strokeStyle = 'red';
        ctx.stroke();
    }
</script>

</body>
</html>

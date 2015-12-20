<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">

    <title>Admin</title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

    <script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css" -->
</head>
<body>
<div class="container">
    <div class="page-header" align="center">
        <h1>Новый объект</h1>
    </div>
    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
        <h4>Введите данные по объекту</h4>

        <div class="form-group input-group">
            <span class="input-group-addon">Схема объекта</span>
            <input id="link" name="link" type="url" class="form-control" onchange="plan()">
        </div>
        <div class="form-group input-group">
            <span class="input-group-addon">Азимут изображения</span>
            <input id="azimuth" name="azimuth" type="number" class="form-control">
        </div>
        <div class="form-group input-group">
            <span class="input-group-addon">Город</span>
            <input id="descr" name="descr" type="text" class="form-control" value="Спб">
            <span class="input-group-addon">Улица, дом</span>
            <input id="name" name="name" type="text" class="form-control" value="Альпийский, 16">
        </div>
        <div class="form-group input-group">
            <span class="input-group-addon">Долгота</span>
            <input id="lon" name="lon" type="number" class="form-control">
            <span class="input-group-addon">Широта</span>
            <input id="lat" name="lat" type="number" class="form-control">
        </div>

        <div class="form-group btn-group-justified">
            <a class="btn btn-primary" onclick="geodecode()">Уточнить</a>
        </div>

        <div class="form-group btn-group-justified">
            <a class="btn btn-primary" onclick="submit()">Сохранить!</a>
        </div>
    </div>



    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
        <img id="plan" class="img-responsive img-rounded" />

        <div id="map" style="width: 100%; height: 500px"/>
    </div>
</div>

<script type="text/javascript">
    ymaps.ready(init);

    var map;

    function init() {
        map = new ymaps.Map("map", {
            center: [55.76, 37.64],
            zoom: 7
        }, {
            searchControlProvider: 'yandex#search'
        });
    }

    function submit() {

    }

    function geodecode() {
        var name = $('#name').val();
        var descr = $('#descr').val();
        var lon = $('#lon').val();
        var lat = $('#lat').val();
        var direct = name && descr;
        if (!direct && !lon && !lat) {
            alert('Заполните либо адрес, либо координаты!');
            return;
        }
        var text = direct ? descr + ', ' + name : lon + ',' + lat;

        $.ajax({
            url: '/geodecode?text=' + text,
            type: 'GET',
            success: function (geoObject) {
                var lon = geoObject.location.coordinates[0];
                var lat = geoObject.location.coordinates[1];
                $('#name').val(geoObject.name);
                $('#descr').val(geoObject.description);
                $('#lon').val(lon);
                $('#lat').val(lat);
                var placemark = new ymaps.Placemark([lat, lon], {
                    // Чтобы балун и хинт открывались на метке, необходимо задать ей определенные свойства.
                    balloonContentHeader: geoObject.name,
                    balloonContentBody: '<img id="plan" src="http://www.okmarket.ru/media/gallery/2012-12-27/okey_tallinskoe_shema_magazina_b.jpg" class="img-responsive img-rounded"/>',
                    balloonContentFooter: text,
                    hintContent: text
                });
                map.geoObjects.removeAll();
                map.geoObjects.add(placemark);
                map.setCenter([lat, lon], 15);
            },
            error: function () {
                if (direct) {
                    $('#lon').val('?');
                    $('#lat').val('?');
                } else {
                    $('#name').val('?');
                    $('#descr').val('?');
                }
            }
        });
    }

    function plan() {
        var link = $('#link').val();
        $('#plan').attr('src', link);
    }
</script>
</body>
</html>

<#--<div class="panel-group" id="accordion">-->
<#--<#list hypermarkets as hypermarket>-->
<#--<div class="panel panel-default">-->
<#--<div class="panel-heading">-->
<#--<h4 class="panel-title">-->
<#--<a data-toggle="collapse" data-parent="#accordion" href="#${hypermarket.id}">-->
<#--<div>-->
<#--Гипермаркет #${hypermarket.id} - ${hypermarket.type} : ${hypermarket.city}-->
<#--</div>-->
<#--</a>-->
<#--</h4>-->
<#--</div>-->
<#--<div id="${hypermarket.id}" class="panel-collapse collapse">-->
<#--<div class="panel-body">-->
<#--<div class="row">-->
<#--<div class="col-md-6">-->
<#--<table class="table-striped" style="width:100%">-->
<#--<tr>-->
<#--<td>Адрес</td>-->
<#--<td>${hypermarket.line}, ${hypermarket.number}</td>-->
<#--</tr>-->
<#--<tr>-->
<#--<td>Координаты(долгота, широта)</td>-->
<#--<td>${hypermarket.location}</td>-->
<#--</tr>-->
<#--<tr>-->
<#--<td>Ссылка на изображение схемы</td>-->
<#--<td><a href="${hypermarket.path}">link</a></td>-->
<#--</tr>-->
<#--<tr>-->
<#--<td>Ссылка на исходную страницу</td>-->
<#--<td><a href="${hypermarket.page}">link</a></td>-->
<#--</tr>-->
<#--<tr>-->
<#--<td>Альтернативная ссылка на схему</td>-->
<#--<td><a href="${hypermarket.url}">link</a></td>-->
<#--</tr>-->
<#--<#if hypermarket.hasOrientation()>-->
<#--<tr>-->
<#--<td>Азимут</td>-->
<#--<td>${hypermarket.orientation}</td>-->
<#--</tr>-->
<#--</#if>-->
<#--</table>-->
<#--</div>-->
<#--<div class="col-md-4">-->
<#--<img src="${hypermarket.path}" class="img-responsive" alt="Responsive image">-->
<#--</div>-->
<#--</div>-->
<#--</div>-->
<#--</div>-->
<#--</div>-->
<#--</#list>-->
<#--</div>-->
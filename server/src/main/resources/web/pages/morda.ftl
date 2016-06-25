<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">

    <title>Админка</title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

    <script src="https://api-maps.yandex.ru/2.1/?lang=${lang}" type="text/javascript"></script>
    <script type="text/javascript" src="//auth.vk.com/js/api/openapi.js?121"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

    <style>
        html, body, #map {
            width: 100%;
            height: 100%;
            padding: 0;
            margin: 0;
        }
        .popover {
            display: block;
            max-width: 800px;
        }
        .close {
            position: absolute;
            right: 5px;
            top: 1px;
        }
    </style>
</head>
<body>

<div id="map"/>

<script>
    <#setting locale="en_US">
    var LANG = '${lang}';
    var CENTER = [${center.latitude}, ${center.longitude}];
    var ZOOM = ${zoom};

    var VK_USER = <#if vk_user??>true<#else>false</#if>;
    var IS_ADMIN = ${is_admin?string('true', 'false')};
</script>

<script src="/web/pages/balloon.js" type="text/javascript"></script>
<script src="/web/pages/morda.js" type="text/javascript"></script>
<script src="/web/pages/edit.js" type="text/javascript"></script>

</body>
</html>

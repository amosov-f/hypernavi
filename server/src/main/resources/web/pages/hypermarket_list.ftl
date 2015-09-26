<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">

    <title>HyperNavi</title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
</head>
<body>
In database ${number} hypermarkets.

<table border="0" style="width:100%">
    <tr>
        <th>Поле:</th>
        <th>Значение:</th>
    </tr>
<#list hypermarkets as hypermarket>
    <tr>
        <td>ID</td>
        <td>${hypermarket.id}</td>
    </tr>
    <tr>
        <td>Адрес</td>
        <td>${hypermarket.address}</td>
    </tr>
    <tr>
        <td>Координаты(долгота, широта)</td>
        <td>${hypermarket.location}</td>
    </tr>
    <tr>
        <td>Картинка</td>
        <td><a href="${hypermarket.path}">link</a></td>
    </tr>
    <tr>
        <td>-------------------------------------------------------</td>
        <td>-------------------------------------------------------</td>
    </tr>
</#list>

</table>


</body>
</html>
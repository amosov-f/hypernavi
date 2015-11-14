<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">

    <title>Admin</title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
</head>
<body>
<h3>In database ${number} hypermarkets.</h3>
<h4> Server initialization time: ${server_starts}</h4>
<div class="panel-group" id="accordion">
<#list hypermarkets as hypermarket>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordion" href="#${hypermarket.id}">
                    <div>
                        Гипермаркет #${hypermarket.id} - ${hypermarket.type} : ${hypermarket.city}
                    </div>
                </a>
            </h4>
        </div>
        <div id="${hypermarket.id}" class="panel-collapse collapse">
            <div class="panel-body">
                <div class="row">
                    <div class="col-md-6">
                        <table class="table-striped" style="width:100%">
                            <tr>
                                <td>Адрес</td>
                                <td>${hypermarket.line}, ${hypermarket.number}</td>
                            </tr>
                            <tr>
                                <td>Координаты(долгота, широта)</td>
                                <td>${hypermarket.location}</td>
                            </tr>
                            <tr>
                                <td>Ссылка на изображение схемы</td>
                                <td><a href="${hypermarket.path}">link</a></td>
                            </tr>
                            <tr>
                                <td>Ссылка на исходную страницу</td>
                                <td><a href="${hypermarket.page}">link</a></td>
                            </tr>
                            <tr>
                                <td>Альтернативная ссылка на схему</td>
                                <td><a href="${hypermarket.url}">link</a></td>
                            </tr>
							<#if hypermarket.hasOrientation()>
                                <tr>
                                    <td>Азимут</td>
                                    <td>${hypermarket.orientation}</td>
                                </tr>
							</#if>
                        </table>
                    </div>
                    <div class="col-md-4">
                        <img src="${hypermarket.path}" class="img-responsive" alt="Responsive image">
                    </div>
                </div>
            </div>
        </div>
    </div>
</#list>
</div>

</body>
</html>
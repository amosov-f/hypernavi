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
<h3>In database ${number} hypermarkets.</h3>

<div class="panel-group" id="accordion">
<#list hypermarkets as hypermarket>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordion" href="#${hypermarket.id}">
                    Гипермаркет #${hypermarket.id}
                </a>
            </h4>
        </div>
        <div id="${hypermarket.id}" class="panel-collapse collapse">
            <div class="panel-body">
                <table class="table-striped" style="width:100%">
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
                </table>
            </div>
        </div>
    </div>
</#list>
</div>

</body>
</html>
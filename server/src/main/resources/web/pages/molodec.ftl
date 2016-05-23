<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">

    <title>Молодцы</title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

    <style>
        .table td {
            text-align: center;
        }

        .table th {
            text-align: center;
        }
    </style>
</head>
<body>

<#assign rub = 67>

<div class="container">
    <h2 align="center">Молодцы</h2>
    <table class="table table-striped fixed center-table">
        <thead>
        <tr>
            <th>#</th>
            <th>Кто</th>
            <th>Размеченных схем</th>
            <th>Всего схем</th>
            <th>Всего подсказок</th>
            <th>Возможный заработок</th>
            <th>Разметка следующей схемы принесет</th>
        </tr>
        </thead>
        <tbody>
        <#list authors as author>
        <tr>
            <th scope="row">${author?index + 1}</th>
            <td><img src="${author.photoRec}" width="40"/></td>
            <#assign score = scores[author?index]>
            <td><b>${score.markedPlans}</b></td>
            <td>${score.plans}</td>
            <td>${score.hints}</td>
            <td>$${score.profit} или ${score.profit * rub} руб.</td>
            <td>$${score.nextCost} или ${score.nextCost * rub} руб.</td>
        </tr>
        </#list>
        </tbody>
    </table>
</div>

</body>
</html>

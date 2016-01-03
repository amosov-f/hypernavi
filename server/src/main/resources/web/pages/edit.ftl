<html lang="ru">
<head>
    <meta charset="utf-8">

    <title>Site ${id}</title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

    <!--link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css"-->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
</head>
<body>
<#assign site = get()>
<h3>${site.position.name}</h3>

<p>${site.position.description}</p>

<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
<#list site.hints as hint>
    <div class="panel panel-default">
        <div class="panel-heading" role="tab" id="headingOne">
            <h4 class="panel-title">
                <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
				${hint.description}
                </a>
            </h4>
        </div>
        <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
            <div class="panel-body">
                <img id="img${id}" class="img-responsive img-rounded" src="${hint.image.link}"/>
                <a class="btn" onclick="edit()">Редактировать</a>
            </div>
        </div>
    </div>
</#list>
</body>
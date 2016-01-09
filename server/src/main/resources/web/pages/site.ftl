<html lang="ru">
<head>
    <meta charset="utf-8">

    <title><#if id??>Site ${id}<#else>New site</#if></title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

<#if debug == true>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
</#if>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
</head>
<body>
<h3>${site.position.name}</h3>

<p>${site.position.description}</p>

<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
    <a class="btn" onclick="edit()">Редактировать</a>
<#list site.hints as hint>
    <div class="panel panel-default">
        <div class="panel-heading" role="tab" id="headingOne">
            <h4 class="panel-title">
                <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true"
                   aria-controls="collapseOne">
					<#if hint.description??>${hint.description}</#if>
                </a>
            </h4>
        </div>
        <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
            <div class="panel-body">
                <img class="img-responsive img-rounded" src="<#if hint.image??>${hint.image.link}<#else>${hint.link}</#if>"/>
            </div>
        </div>
    </div>
</#list>
</div>
</body>
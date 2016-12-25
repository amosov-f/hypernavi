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
<h3>${site.place.name}</h3>

<p>${site.place.description}</p>

<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
    <div class="form-group">
    <#if is_admin>
        <a class="btn btn-default" onclick="edit()">Редактировать</a>
    </#if>
    </div>
<#list site.hints as hint>
    <div class="panel panel-default">
        <div class="panel-heading" role="tab" id="headingOne">
            <h4 class="panel-title">
                <a href="#${hint?index}" role="button" data-toggle="collapse" data-parent="#accordion" aria-expanded="true" aria-controls="collapseOne">
					<#if hint.type == 'PLAN'>
                        Схема
					<#elseif hint.type == 'PICTURE'>
                        Изображение
					</#if>
                </a>
            </h4>
        </div>
        <div id="${hint?index}" class="panel-collapse collapse <#if hint?index == 0>in</#if>" role="tabpanel" aria-labelledby="headingOne">
            <div class="panel-body">
                <div class="form-group">
                    <img class="img-responsive img-rounded" src="${hint.image.link}"/>
                </div>
				<#if hint.description??>
                    <div class="well">${hint.description}</div>
				</#if>
				<#if hint.type == 'PLAN' && hint.points?has_content>
                    <h4 align="center">Markup</h4>
                    <table class="table table-striped fixed">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Latitude</th>
                            <th>Longitude</th>
                            <th>X</th>
                            <th>Y</th>
                        </tr>
                        </thead>
                        <tbody>
							<#list hint.points as point>
                            <tr>
                                <th scope="row">${point?index + 1}</th>
                                <td>${point.geoPoint.latitude}</td>
                                <td>${point.geoPoint.longitude}</td>
                                <td>${point.mapPoint.x}</td>
                                <td>${point.mapPoint.y}</td>
                            </tr>
							</#list>
                        </tbody>
                    </table>
				</#if>
            </div>
        </div>
    </div>
</#list>
</div>
</body>
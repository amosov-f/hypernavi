<html lang="ru">
<head>
    <meta charset="utf-8">

    <title><#if id??>Site ${id}<#else>New site</#if></title>

<#setting locale="en_US">
<#setting number_format="0.######">

<#if debug == true>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

    <script src="/web/pages/edit.js" type="text/javascript"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
</#if>
</head>
<body>

<h3>${site.place.name}</h3>
<p>${site.place.description}</p>
<div class="form-group">
    <a class="btn btn-primary" onclick="onSubmit()">Сохранить</a>
    <a class="btn btn-danger" onclick="onSiteRemove('${id!''}')">Удалить</a>
    <a class="btn btn-default" onclick="onSubmitSuccess()">Отмена</a>
    <a class="btn btn-default pull-right" onclick="showOnGoogleMaps()">
        <img src="http://www.google.ru/favicon.ico" height="18">
    </a>
    <a class="btn btn-default pull-right" onclick="showOnYandexMaps()">
        <img src="http://yandex.ru/favicon.ico" height="18">
    </a>
</div>

<#assign hints = site.hints>
<#if !hints?has_content>
	<#assign hints = hints + [{
        'type': 'PLAN',
        'image': {
            'link': ''
        },
        'points': []
    }]>
</#if>

<div class="panel-group" id="accordion">
<#list hints as hint>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a href="#${hint?index}" class="accordion-toggle" data-toggle="collapse" data-parent="#accordion">
                    Подсказка #${hint?index + 1}
                </a>
            </h4>
        </div>
        <div id="${hint?index}" class="panel-collapse collapse <#if hint?index == 0>in</#if>">
            <div class="panel-body">
                <div class="form-group">
                    <button class="btn" onclick="removeHint('${hint?index}')">
                        <i class="glyphicon glyphicon-minus"></i> Удалить подсказку
                    </button>
                </div>
                <div class="form-group input-group">
                    <span class="input-group-addon">Описание</span>
                    <textarea class="form-control" rows="2"><#if hint.description??>${hint.description}</#if></textarea>
                </div>
                <div class="form-group">
                    <input type="hidden" <#if hint.authorUid??>value="${hint.authorUid}"</#if>>
                </div>
                <ul class="nav nav-tabs" role="tablist">
                    <li role="presentation" <#if hint.type == 'PLAN'>class="active"</#if>>
                        <a href="#plan${hint?index}" data-toggle="tab">Схема</a>
                    </li>
                    <li role="presentation" <#if hint.type == 'PICTURE'>class="active"</#if>>
                        <a href="#picture${hint?index}" data-toggle="tab">Изображение</a>
                    </li>
                </ul>
                <div class="tab-content">
                    <div id="plan${hint?index}" role="tabpanel" class="tab-pane fade <#if hint.type == 'PLAN'>in active</#if>">
                        <div class="form-group">
                            <div class="form-group col-lg-9">
                                <div class="input-group">
                                    <span class="input-group-addon">Ссылка</span>
                                    <input type="url" class="form-control" <#if hint.type == 'PLAN'>value="${hint.image.link}"</#if>>
                                </div>
                            </div>
                            <div class="form-group col-lg-3">
                                <a class="btn btn-default" onclick="sync('plan${hint?index}')">Показать</a>
                                <a class="btn btn-default" onclick="markup(this, ${hint?index})">
                                    <i class="glyphicon glyphicon-screenshot"></i>
                                </a>
                            </div>
                        </div>
                        <div class="form-group col-lg-12">
                            <div class="form-group input-group">
                                <span class="input-group-addon">Азимут</span>
                                <input type="number" class="form-control" <#if hint.azimuth??>value="${hint.azimuth}"</#if>>
                                <span class="input-group-addon">градусов</span>
                            </div>
                        </div>
                        <img class="img-responsive img-rounded" <#if hint.type == 'PLAN'>src="${hint.image.link}"</#if>/>
                        <p id="imgsize${hint?index}" align="right"></p>
                        <h4 align="center">Разметка схемы</h4>
                        <table class="table table-striped fixed">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Широта,Долгота</th>
                                <th></th>
                                <th>X,Y</th>
                                <th></th>
                                <th>Ошибка по X,Y</th>
                            </tr>
                            </thead>
                            <tbody id="points${hint?index}">
                                <#assign points = hint.points>
                                <#if !points?has_content>
                                    <#assign points = points + [{}]>
                                </#if>
                                <#list points as point>
                                <tr>
                                    <th scope="row">${point?index + 1}</th>
                                    <td><input class="form-control" <#if point.geoPoint??>value="${point.geoPoint.latitude},${point.geoPoint.longitude}"</#if>></td>
                                    <td>
                                        <button class="btn btn-default" onclick="showPointOnYandexMaps(this)">
                                            <img src="http://yandex.ru/favicon.ico" height="18">
                                        </button>
                                        <button class="btn btn-default" onclick="showPointOnGoogleMaps(this)">
                                            <img src="http://www.google.ru/favicon.ico" height="18">
                                        </button>
                                    </td>
                                    <td><input class="form-control" <#if point.mapPoint??>value="${point.mapPoint.x},${point.mapPoint.y}"</#if>></td>
                                    <td>
                                        <button class="btn btn-default" onclick="markupXY(this)">
                                            <i class="glyphicon glyphicon-screenshot"></i>
                                        </button>
                                    </td>
                                    <td><p></p></td>
                                </tr>
                                </#list>
                            </tbody>
                        </table>
                        <button class="btn btn-default" onclick="addPointMap(${hint?index})">
                            <i class="glyphicon glyphicon-plus"></i> Еще точка
                        </button>
                        <button class="btn btn-default" onclick="validatePlanPoints(${hint?index})">
                            <i class="glyphicon glyphicon-play"></i> Провалидировать
                        </button>
                        <br></br>
                        <p id="eval${hint?index}"></p>
                    </div>
                    <div id="picture${hint?index}" role="tabpanel" class="tab-pane fade <#if hint.type == 'PICTURE'>in active</#if>">
                        <div class="form-group">
                            <div class="form-group col-lg-8">
                                <div class="input-group">
                                    <span class="input-group-addon">Ссылка</span>
                                    <input type="url" class="form-control" <#if hint.type == 'PICTURE'>value="${hint.image.link}"</#if>>
                                </div>
                            </div>
                            <a class="btn btn-default col-lg-2" onclick="sync('picture${hint?index}')">Показать</a>
                        </div>
                        <img class="img-responsive img-rounded" <#if hint.type == 'PICTURE'>src="${hint.image.link}"</#if>/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</#list>
</div>
<button class="btn btn-default" onclick="addHint()">
    <i class="glyphicon glyphicon-plus"></i> Новая подсказка
</button>

<script>
    var INDEX = ${hints?size};
    var $TEMPLATE = $(".panel").first();

    function onSubmit() {
        onSiteSubmit({
            'id': '${id!''}',
            'type': 'site',
            'place': {
                'name': '${site.place.name}',
                'description': '${site.place.description}',
                'location': {
                    'type': 'Point',
                    'coordinates': [${site.place.location.longitude}, ${site.place.location.latitude}]
                }
            }
        });
    }

    function showOnYandexMaps() {
        showLatLonOnYandexMaps(${site.place.location.latitude} + ',' + ${site.place.location.longitude});
    }

    function showOnGoogleMaps() {
        showLatLonOnGoogleMaps(${site.place.location.latitude} + ',' + ${site.place.location.longitude});
    }
</script>

</body>
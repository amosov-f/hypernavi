<html lang="ru">
<head>
    <meta charset="utf-8">

    <title><#if id??>Site ${id}<#else>New site</#if></title>

<#setting locale="en_US">
<#setting number_format="0.######">

<#if debug == true>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
</#if>
</head>
<body>
<h3>${site.place.name}</h3>

<p>${site.place.description}</p>

<div class="panel-group" id="accordion">
    <div class="form-group">
        <a class="btn btn-primary" onclick="onSubmit()">Сохранить</a>
        <a class="btn btn-danger" onclick="onRemove()">Удалить</a>
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
                            <div class="form-group col-lg-10">
                                <div class="input-group">
                                    <span class="input-group-addon">Ссылка</span>
                                    <input type="url" class="form-control" <#if hint.type == 'PLAN'>value="${hint.image.link}"</#if>>
                                </div>
                            </div>
                            <a class="btn btn-default col-lg-2" onclick="sync('plan${hint?index}')">Показать</a>
                        </div>
                        <div class="form-group col-lg-12">
                            <div class="form-group input-group">
                                <span class="input-group-addon">Азимут</span>
                                <input type="number" class="form-control" <#if hint.azimuth??>value="${hint.azimuth}"</#if>>
                                <span class="input-group-addon">градусов</span>
                            </div>
                        </div>
                        <img class="img-responsive img-rounded" <#if hint.type == 'PLAN'>src="${hint.image.link}"</#if>/>
                        <#if hint.type == 'PLAN'>
                            <h4 align="center">Разметка схемы</h4>
                            <table class="table table-striped fixed">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Широта,Долгота</th>
                                    <th>X,Y</th>
                                    <th>Ошибка в пикс.</th>
                                </tr>
                                </thead>
                                <tbody id="points${hint?index}">
                                    <#list hint.points as point>
                                    <tr>
                                        <th scope="row">${point?index + 1}</th>
                                        <td><input class="form-control" value="${point.geoPoint.latitude},${point.geoPoint.longitude}"></td>
                                        <td><input class="form-control" value="${point.mapPoint.x},${point.mapPoint.y}"></td>
                                        <td></td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </#if>
                        <button class="btn btn-default" onclick="addPointMap(${hint?index})">
                            <i class="glyphicon glyphicon-plus"></i> Новая точка
                        </button>
                        <button class="btn btn-default" onclick="validate(${hint?index})">
                            <i class="glyphicon glyphicon-play"></i> Провалидировать
                        </button>
                    </div>
                    <div id="picture${hint?index}" role="tabpanel" class="tab-pane fade <#if hint.type == 'PICTURE'>in active</#if>">
                        <div class="form-group">
                            <div class="form-group col-lg-10">
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
    var $TEMPLATE = $(".panel").first();
    var INDEX = ${hints?size};

    function addHint() {
        var $newHint = $TEMPLATE.clone();

        $newHint.find(':input').val('');
        $newHint.find('img').attr('src', '');
        $newHint.find('textarea').val();

        $newHint.find(".accordion-toggle")
                .attr("href", "#" + INDEX)
                .text("Подсказка #" + (INDEX + 1));
        $newHint.find('.panel-collapse')
                .attr('id', INDEX)
                .removeClass('in');

        $newHint.find('a[href^=#plan]').attr('href', '#plan' + INDEX);
        $newHint.find('a[href^=#picture]').attr('href', '#picture' + INDEX);
        $newHint.find('div[id^=plan]').attr('id', 'plan' + INDEX);
        $newHint.find('div[id^=picture]').attr('id', 'picture' + INDEX);

        $newHint.find('a[onclick^=sync]').attr('onclick', "sync('plan" + INDEX + "')");
        $newHint.find('a[onclick^=sync]').attr('onclick', "sync('picture" + INDEX + "')");
        $newHint.find('button[onclick^=removeHint]').attr('onclick', "removeHint('" + INDEX + "')");

        $newHint.find('tbody').attr('id', 'points' + INDEX);
        $newHint.find('button[onclick^=addPointMap]').attr('onclick', "addPointMap('" + INDEX + "')");

        $("#accordion").append($newHint.fadeIn());

        INDEX++;
    }

    function removeHint(index) {
        $('#' + index).parent().remove();
    }

    function sync(tabId) {
        var $tab = $('#' + tabId);
        var link = $tab.find(':input[type=url]').val();
        $tab.find('img').attr('src', link);
        console.log($tab.find('img').attr('src'));
        refresh();
        console.log($tab.find('img').attr('src'));
    }

    function onSubmit() {
        var hints = $('.panel-collapse').map(function() {
            var hint = {};

            var description = $(this).find('textarea').val();
            if (description.length != 0) {
                hint.description = description;
            }
            var authorUid = $(this).find(':input[type=hidden]').val();
            if (authorUid) {
                hint.authorUid = authorUid;
            }

            var index = $(this).attr('id');
            var $hint = $('#plan' + index);
            hint.type = 'plan';
            if (!$hint.hasClass('active')) {
                $hint = $('#picture' + index);
                hint.type = 'picture';
            }

            var link = $hint.find(':input[type=url]').val();
            hint.image = duplicate(link);

            if (hint.type == 'plan') {
                var azimuth = $hint.find(':input[type=number]').val();
                if (azimuth.length != 0) {
                    hint.azimuth = parseFloat(azimuth);
                }

                hint.points = extractPoints($hint);
                check(hint.points);
            }

            return hint;
        }).get();

        var site = {
            type: 'site',
            place: {
                name: '${site.place.name}',
                description: '${site.place.description}',
                location: {
                    type: 'Point',
                    coordinates: [${site.place.location.longitude}, ${site.place.location.latitude}]
                }
            },
            hints: hints
        };
    <#if id??>
        site.id = '${id}';
    </#if>
        var put = !site.id;
        var path = put ? '/admin/site/put' : '/admin/site/edit';
        $.ajax({
            url: path,
            data: JSON.stringify(site),
            type: 'POST',
            contentType: "application/json; charset=utf-8",
            success: function (id) {
                onSubmitSuccess(put ? id : null)
            },
            error: function (req, textStatus, error) {
                alert('Ошибка! ' + error)
            }
        });
    }

    function extractPoints($html) {
        return $html.find('tr').map(function (i) {
            var latLon = $(this).find('input').eq(0).val();
            var xy = $(this).find('input').eq(1).val();
            if (!latLon && !xy) {
                return null;
            }
            var lat = parseFloat(latLon.split(',')[0]);
            var lon = parseFloat(latLon.split(',')[1]);
            var x = parseInt(xy.split(',')[0]);
            var y = parseInt(xy.split(',')[1]);
            var message = null;
            if (isNaN(x) || isNaN(y)) {
                message = "Неверный формат 'X,Y': '" + xy + "'!";
            }
            if (isNaN(lat) || isNaN(lon)) {
                message = "Неверный формат 'Широты,Долготы': '" + latLon + "'!";
            }
            if (message != null) {
                alert(message);
                throw message;
            }
            return {
                no: i,
                geoPoint: { type: 'Point', coordinates: [ lon, lat ] },
                mapPoint: { x: x, y: y }
            }
        }).get()
    }

    function addPointMap(hintIndex) {
        var $points = $('#points' + hintIndex);
        var no = $points.children('tr').length + 1;
        var row = '<tr><th scope="row">' + no + '</th><td><input class="form-control"></td><td><input class="form-control"></td><td></td></tr>';
        $points.append(row)
    }

    function duplicate(link) {
        var image;
        $.ajax({
            url: '/admin/image/duplicate?link=' + encodeURIComponent(link),
            async: false,
            success: function (duplicatedImage) {
                image = duplicatedImage;
            },
            error: function (req, textStatus, error) {
                alert('Ошибка! ' + error);
                throw error;
            }

        });
        return image;
    }

    function validate(hintIndex) {
        var $points = $('#points' + hintIndex);
        var points = extractPoints($points);
        check(points);
        $points.find('tr').each(function () {
            $($(this).find('td')[2]).html('');
        });
        $.ajax({
            url: '/admin/validate',
            data: JSON.stringify(points),
            type: 'POST',
            contentType: "application/json; charset=utf-8",
            success: function (distances) {
                points.forEach(function (point, i) {
                    $($($points.find('tr')[point.no]).find('td')[2]).html(Math.round(distances[i]));
                });
            },
            error: function (req, textStatus, error) {
                alert('Ошибка! ' + error)
            }
        });
    }

    function check(points) {
        if (0 < points.length && points.length < 3) {
            var message = 'Точек должно быть хотя бы три!';
            alert(message);
            throw message;
        }
    }

    function onRemove() {
    <#if id??>
        $.ajax({
            url: url('/admin/site/remove', 'site_id', '${id}'),
            type: 'GET',
            success: function (resp, textStatus, req) {
                alert('Объект ' + '${id}' + ' успешно удален!');
                removeSite();
            },
            error: function (req, textStatus, error) {
                alert('Ошибка! ' + error)
            }
        });
    <#else>
        removeSite();
    </#if>
    }

    function url(path, paramName, paramValue) {
        var search = $(location).attr('search');
        return path + search + (search.length == 0 ? '?' : '&') + paramName + '=' + paramValue;
    }
</script>

</body>
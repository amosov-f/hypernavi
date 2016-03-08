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
        }
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
                            <div class="form-group input-group ">
                                <span class="input-group-addon">Азимут</span>
                                <input type="number" class="form-control" <#if hint.azimuth??>value="${hint.azimuth}"</#if>>
                                <span class="input-group-addon">градусов</span>
                            </div>
                        </div>
                        <img class="img-responsive img-rounded" <#if hint.type == 'PLAN'>src="${hint.image.link}"</#if>/>
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

            var index = $(this).attr('id');
            var $hint = $('#plan' + index);
            hint.type = 'plan';
            if (!$hint.hasClass('active')) {
                $hint = $('#picture' + index);
                hint.type = 'picture';
            }

            var link = $hint.find(':input[type=url]').val();
            hint.image = {
                link: link,
                dimension: dimension(link),
                duplicates: []
            };

            if (hint.type == 'plan') {
                var azimuth = $hint.find(':input[type=number]').val();
                if (azimuth.length != 0) {
                    hint.azimuth = parseFloat(azimuth);
                }
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
            url: url(path, put ? 'site' : 'site_index', encodeURIComponent(JSON.stringify(site))),
            type: 'GET',
            success: function (id) {
                onSubmitSuccess(put ? id : null)
            },
            error: function (req, textStatus, error) {
                alert('Ошибка! ' + error)
            }
        });
    }

    function dimension(link) {
        var img = new Image();
        img.src = link;
        return {
            width: img.width,
            height: img.height
        };
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

function addHint() {
    var $newHint = $TEMPLATE.clone();

    $newHint.find(':input').val('');
    $newHint.find('img').attr('src', '');
    $newHint.find('p[id^=imgsize]').attr('id', 'imgsize' + INDEX);
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
    $newHint.find('button[onclick^=validatePlanPoints]').attr('onclick', "validatePlanPoints('" + INDEX + "')");

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
    refresh();
}

function markup(button, hintIndex) {
    var points = extractPoints($('#points' + hintIndex)).map(function (p) {
        return p.mapPoint;
    });
    var link = $(button).parents('.tab-pane').find(':input[type=url]').val();
    var url = '/admin/markup?link=' + encodeURIComponent(link) + '&points=' + JSON.stringify(points);

    window.open(url, '_blank');
}

function markupXY(button) {
    var $button = $(button);
    var xy = $button.parent().parent().find('input').eq(1).val();
    var x = xy.split(',')[0];
    var y = xy.split(',')[1];
    var link = $button.parents('.tab-pane').find('img').attr('src');
    window.open('/admin/markup?link=' + link + '&x=' + x + '&y=' + y, '_blank')
}

function showPointOnYandexMaps(button) {
    showLatLonOnYandexMaps($(button).parent().parent().find('input').eq(0).val())
}

function showLatLonOnYandexMaps(latLon) {
    window.open('https://yandex.ru/maps/?text=' + latLon, '_blank');
}

function showPointOnGoogleMaps(button) {
    showLatLonOnGoogleMaps($(button).parent().parent().find('input').eq(0).val())
}

function showLatLonOnGoogleMaps(latLon) {
    window.open('https://google.com/maps/?q=' + latLon, '_blank');
}

function onSiteSubmit(site) {
    site.hints = $('.panel-collapse').map(function() {
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

        var link = imageLink($hint);
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

function imageLink($hint) {
    return $hint.find(':input[type=url]').val();
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
    var $rows = $points.find('tr');
    var $newPoint = $rows.eq(0).clone();
    $newPoint.find('th').html($rows.length + 1);
    $newPoint.find('input').val('');
    $newPoint.find('p').html('');
    console.log($newPoint);
    $points.append($newPoint)
}

function imageSize(link) {
    var imageSize;
    $.ajax({
        url: '/admin/image/size?link=' + encodeURIComponent(link),
        async: false,
        success: function (size) {
            imageSize = size;
        },
        error: function (req, textStatus, error) {
            alert('Ошибка! ' + error);
            throw error;
        }

    });
    return imageSize;
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

function validatePlanPoints(hintIndex) {
    var planLink = imageLink($('#plan' + hintIndex));
    if (!planLink) {
        alertAndThrow('Сначала укажите ссылку на схему!')
    }
    var planSize = imageSize(planLink);
    var w = planSize.dimension.width;
    var h = planSize.dimension.height;
    var kb = parseInt(planSize.fileSize / 1000, 10);
    $('#imgsize' + hintIndex).html(w + ' &times; ' + h + ' px = ' + kb + ' kb');

    var $points = $('#points' + hintIndex);
    var $eval = $('#eval' + hintIndex);

    var points = extractPoints($points);
    check(points);

    $points.find('p').html('');
    $eval.html('');
    $.ajax({
        url: '/admin/validate',
        data: JSON.stringify(points),
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        success: function (validation) {
            points.forEach(function (point, i) {
                var dx = Math.round(100 * validation.diffs[i].x / w);
                var dy = Math.round(100 * validation.diffs[i].y / h);
                var dxFont = '<font color="' + errorColor(dx) + '">' + dx + '%</font>';
                var dyFont = '<font color="' + errorColor(dy) + '">' + dy + '%</font>';
                $points.find('tr').eq(point.no).find('p').html(dxFont + ',' + dyFont);
                var evalX = '<b>X</b><xmp>' + validation.evalX + '</xmp>';
                var evalY = '<b>Y</b><xmp>' + validation.evalY + '</xmp>';
                $eval.html(evalX + evalY);
            });
        },
        error: function (req, textStatus, error) {
            alert('Ошибка! ' + error)
        }
    });
}

function errorColor(num) {
    var abs = Math.abs(num);
    return abs <= 3 ? 'green' : abs <= 6 ? '#99cc00' : abs <= 10 ? 'orange' : 'red';
}

function check(points) {
    if (0 < points.length && points.length < 3) {
        alertAndThrow('Точек должно быть хотя бы три!')
    }
}

function alertAndThrow(message) {
    alert(message);
    throw message;
}

function onSiteRemove(siteId) {
    if (!siteId) {
        removeSite();
        return;
    }
    $.ajax({
        url: url('/admin/site/remove', 'site_id', siteId),
        type: 'GET',
        success: function (resp, textStatus, req) {
            alert('Объект ' + siteId + ' успешно удален!');
            removeSite();
        },
        error: function (req, textStatus, error) {
            alert('Ошибка! ' + error)
        }
    });
}

function url(path, paramName, paramValue) {
    var search = $(location).attr('search');
    return path + search + (search.length == 0 ? '?' : '&') + paramName + '=' + paramValue;
}
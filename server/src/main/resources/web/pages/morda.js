var site, objectManager;
var ID = 0;

ymaps.ready(function () {
    var map = new ymaps.Map('map', {
        center: CENTER,
        zoom: ZOOM,
        controls: ['zoomControl', 'searchControl', 'typeSelector',  'fullscreenControl']
    }, {
        noPlacemark: true,
        searchControlProvider: 'yandex#search'
    });

    addLangButton(map);
    if (!VK_USER) {
        addVkAuthButton(map);
    }

    objectManager = new ymaps.ObjectManager({
        clusterize: true,
        gridSize: 32
    });
    objectManager.objects.options.set('preset', 'islands#greenDotIcon');
    objectManager.clusters.options.set('preset', 'islands#greenClusterIcons');
    map.geoObjects.add(objectManager);

    $.ajax({
        url: "/search?lon=30&lat=60&ns=100",
        success: function (searchData) {
            objectManager.add(convert(searchData));
        }
    });

    objectManager.events.add('balloonopen', function (e) {
        site = objectManager.objects.getById(e.get('objectId'));
        if (!site.properties.balloonContent) {
            site.properties.balloonContent = balloonContent(site.raw);
            if (site.properties.balloonContent != null) {
                refresh();
            }
        }
    });

    objectManager.events.add('balloonclose', function (e) {
        site = null;
    });

    var searchControl = map.controls.get('searchControl');
    searchControl.events.add('resultselect', function (e) {
        searchControl.getResult(e.get('index')).then(function (res) {
            var hasSuchSite = false;
            objectManager.objects.each(function(site) {
                hasSuchSite |= equals(site.geometry.coordinates, res.geometry.getCoordinates());
            });
            if (hasSuchSite) {
                alert('Такой объект уже есть!');
                return;
            }
            var rawSite = {
                type: 'site',
                place: {
                    name: res.properties.get('name'),
                    description: res.properties.get('description'),
                    location: reverseToPoint(res.geometry.getCoordinates())
                },
                hints: []
            };
            objectManager.add(convert({data: {sites: [rawSite]}}));
        });
    });
});

function addLangButton(map) {
    var changeLangButton = new ymaps.control.Button({
        data: {
            content: 'Сменить язык'
        },
        options: {
            maxWidth: 150
        }
    });
    changeLangButton.events.add('click', function () {
        var lat = map.getCenter()[0];
        var lon = map.getCenter()[1];
        var lang = LANG === 'ru_RU' ? 'en_US' : 'ru_RU';
        location.href = '/admin?lang=' + lang + '&lat=' + lat + '&lon=' + lon + '&zoom=' + map.getZoom();
    });
    map.controls.add(changeLangButton);
}

function vkAuthButton() {
    var vkAuthButton = new ymaps.control.Button({
        data: {
            content: 'Войти через ВКонтакте'
        },
        options: {
            maxWidth: 200
        }
    });
    vkAuthButton.events.add('click', function() {
        location.href = '/auth?url=' + location.href;
    });
    return vkAuthButton;
}

function addVkAuthButton(map) {
    var button = vkAuthButton();
    map.controls.add(button, {float: 'right'});
}

function convert(searchData) {
    var features = [];
    searchData.data.sites.forEach(function (rawSite) {
        features.push(feature(rawSite));
    });
    return {
        type: "FeatureCollection",
        features: features
    };
}

function feature(rawSite) {
    var hasPoints = false;
    rawSite.hints.forEach(function (hint) {
        if (hint.type === 'plan') {
            hasPoints |= hint.points.length > 0;
        }
    });
    return {
        type: "Feature",
        id: ID++,
        geometry: reverseToPoint(rawSite.place.location.coordinates),
        properties: {
        },
        options: {
            preset: hasPoints ? 'islands#blueDotIcon' : 'islands#greenDotIcon',
            balloonShadow: false,
            balloonLayout: BalloonLayout,
            balloonContentLayout: BalloonContentLayout,
            balloonPanelMaxMapArea: 0
        },
        raw: rawSite
    };
}

function balloonContent(rawSite) {
    var absent = !rawSite.id;
    if (absent) {
        if (!VK_USER) {
            return '<a class="btn btn-default" href="/auth?url=' + location.href + '">Войти через ВКонтакте</a>';
        }
        if (!IS_ADMIN) {
            return "Только админы могут добавлять новые объекты!<br>" +
                    "Обратитесь к <a href='https://vk.com/id98810985'>Федору</a>, чтобы стать админом";
        }
    }
    var balloonContent = null;
    $.ajax({
        url: absent ? '/site/edit' : url('/site', 'site_id', rawSite.id),
        data: absent ? JSON.stringify(rawSite) : null,
        type: absent ? 'POST' : 'GET',
        contentType: "application/json; charset=utf-8",
        async: false,
        success: function(html) {
            balloonContent = html;
        },
        error: function (xhr, ajaxOptions, thrownError) {
            alert(xhr.status);
            alert(thrownError);
        }
    });
    return balloonContent;
}

function refresh() {
    objectManager.objects.balloon.open(site.id);
    evalScripts(site.properties.balloonContent);
}

function evalScripts(content) {
    $(content).filter('script').each(function () {
        if (this.src) {
            $.ajax({
                async: false,
                url: this.src,
                dataType: "script"
            });
        }
        if (this.innerHTML) {
            $.globalEval(this.innerHTML)
        }
    });
}

function edit() {
    $.ajax({
        url: url('/site/edit', 'site_id', site.raw.id),
        async: false,
        success: function(html) {
            site.properties.balloonContent = html;
            refresh();
        }
    });
}

function removeSite() {
    objectManager.objects.remove(objectManager.objects.getById(site.id));
    site = null;
}

function onSubmitSuccess(id) {
    site.properties.balloonContent = null;
    if (id) {
        site.raw.id = id;
    }
    refresh();
}

function url(path, paramName, paramValue) {
    var search = $(location).attr('search');
    return path + search + (search.length == 0 ? '?' : '&') + paramName + '=' + paramValue;
}

function reverseToPoint(coordinates) {
    return {
        type: 'Point',
        coordinates: [coordinates[1], coordinates[0]]
    };
}

function equals(a, b) {
    return a.toString() == b.toString();
}

function updateQueryStringParameter(uri, key, value) {
    var re = new RegExp("([?&])" + key + "=.*?(&|$)", "i");
    var separator = uri.indexOf('?') !== -1 ? "&" : "?";
    if (uri.match(re)) {
        return uri.replace(re, '$1' + key + "=" + value + '$2');
    } else {
        return uri + separator + key + "=" + value;
    }
}
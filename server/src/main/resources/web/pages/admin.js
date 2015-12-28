var site, objectManager;
var ID = 0;

ymaps.ready(function () {
    var map = new ymaps.Map('map', {
        center: [59.796, 30.1466],
        zoom: 9
    }, {
        noPlacemark: true,
        searchControlProvider: 'yandex#search'
    });

    objectManager = new ymaps.ObjectManager({
        clusterize: true,
        gridSize: 32
    });
    objectManager.objects.options.set('preset', 'islands#greenDotIcon');
    objectManager.clusters.options.set('preset', 'islands#greenClusterIcons');
    map.geoObjects.add(objectManager);

    console.log($(location));

    $.ajax({
        url: "/search?lon=30&lat=60&ns=100",
        success: function (searchData) {
            objectManager.add(convert(searchData));
            objectManager.events.add('balloonopen', function (e) {
                site = objectManager.objects.getById(e.get('objectId'));
            });
        }
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
                position: {
                    name: res.properties.get('name'),
                    description: res.properties.get('description'),
                    location: reverseToPoint(res.geometry.getCoordinates())
                },
                plans: [{link: ''}]
            };
            objectManager.add(convert({data: {sites: [rawSite]}}));
        });
    });
});

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
    return {
        type: "Feature",
        id: ID++,
        geometry: reverseToPoint(rawSite.position.location.coordinates),
        properties: {
            balloonContent: balloonContent(rawSite)
        },
        options: {
            balloonShadow: false,
            balloonLayout: BalloonLayout,
            balloonContentLayout: BalloonContentLayout,
            balloonPanelMaxMapArea: 0
        },
        raw: rawSite
    };
}

function balloonContent(rawSite) {
    return '<h3>' + rawSite.position.name + '</h3>' +
           '<p>' + rawSite.position.description + '</p>' +
           '<img id="img' + rawSite.id + '"class="img-responsive img-rounded" src="' + rawSite.plans[0].link + '"/>' +
           '<a class="btn" onclick="edit()">Редактировать</a>';
}

function refresh() {
    objectManager.objects.balloon.open(site.id);
}

function edit() {
    site.properties.balloonContent = editBalloonContent();
    refresh();
}

function editBalloonContent() {
    var rawSite = site.raw;
    return '<h3>' + rawSite.position.name + '</h3>' +
           '<p>' + rawSite.position.description + '</p>' +
           '<input id="link" type="url" class="form-control" value="' + link() + '">' +
           '<a class="btn" onclick="sync()">Показать</a>' +
           '<img id="img' + site.id + '" class="img-responsive img-rounded" src="' + link() + '"/>' +
           '<a class="btn" onclick="onSubmit()">Сохранить</a>' +
           '<a class="btn" onclick="onRemove()">Удалить</a>';
}

function link() {
    var link = $('#link').val();
    if (link != null) {
        return link;
    }
    return site.raw.plans[0].link;
}

function sync() {
    site.properties.balloonContent = editBalloonContent();
    refresh();
}

function onRemove() {
    var id = site.raw.id;
    if (id) {
        $.ajax({
            url: '/admin/site/remove' + $(location).attr('search') + '&site_id=' + id,
            type: 'GET',
            success: function (resp, textStatus, req) {
                alert('Объект ' + id + ' успешно удален!');
                removeSite();
                site = null;
            },
            error: function (req, textStatus, error) {
                alert('Ошибка! ' + error)
            }
        });
    }
}

function removeSite() {
    objectManager.objects.remove(objectManager.objects.getById(site.id));
}

function onSubmit() {
    var rawSite = site.raw;
    rawSite.plans[0].link = $('#link').val();
    var add = !rawSite.id;
    var path = add ? '/admin/site/add' : '/admin/site/edit';
    var param = add ? 'site' : 'site_index';
    $.ajax({
        url: path + $(location).attr('search') + '&' + param + '=' + JSON.stringify(rawSite),
        type: 'GET',
        success: function (id) {
            site.properties.balloonContent = balloonContent(rawSite);
            if (add) {
                removeSite();
                site.raw.id = id;
                objectManager.objects.add(site);
            }
            refresh();
        },
        error: function (req, textStatus, error) {
            alert('Ошибка! ' + error)
        }
    });
    refresh();
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
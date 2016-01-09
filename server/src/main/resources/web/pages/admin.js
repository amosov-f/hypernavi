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

    $.ajax({
        url: "/search?lon=30&lat=60&ns=100",
        success: function (searchData) {
            objectManager.add(convert(searchData));
        }
    });

    objectManager.events.add('balloonopen', function (e) {
        site = objectManager.objects.getById(e.get('objectId'));
        if (!site.properties.balloonContent) {
            console.log(site.raw.id);
            site.properties.balloonContent = balloonContent(site.raw);
            refresh();
        }
    });

    objectManager.events.add('balloonclose', function (e) {
        console.log('nul!');
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
                position: {
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
    var absent = !rawSite.id;
    var balloonContent;
    $.ajax({
        url: url('/admin/site', absent ? 'site' : 'site_id', absent ? JSON.stringify(rawSite) : rawSite.id),
        async: false,
        success: function(html) {
            balloonContent = html;
        }
    });
    return balloonContent;
}

function refresh() {
    objectManager.objects.balloon.open(site.id);
    $(site.properties.balloonContent).filter('script').each(function() {
        $.globalEval(this.text);
    });
}

function edit() {
    $.ajax({
        url: url('/admin/site', 'edit=1&site_id', site.raw.id),
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
    console.log('pizza: ' + id);
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
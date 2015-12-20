var site, objectManager, placemark;

ymaps.ready(function () {
    // Создание экземпляра карты и его привязка к созданному контейнеру.
    var map = new ymaps.Map('map', {
        center: [55.751574, 37.573856],
        zoom: 9,
        behaviors: ['default', 'scrollZoom']
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
            objectManager.events.add('balloonopen', function (e) {
                site = objectManager.objects.getById(e.get('objectId'));
            });
        }
    });
    var searchControl = new ymaps.control.SearchControl({
        options: {
            noPlacemark: true,
            searchControlProvider: 'yandex#search'
        }
    });
    map.controls.add(searchControl);
    searchControl.events.add('resultselect', function (e) {
        searchControl.getResult(e.get('index')).then(function (res) {
            console.log(res.geometry.getCoordinates());
            objectManager.add(convert({
                data: {
                    sites: [{
                        id: '',
                        type: 'site',
                        position: {
                            name: res.properties.get('name'),
                            description: res.properties.get('description'),
                            location: {
                                type: 'Point',
                                coordinates: res.geometry.getCoordinates()
                            }
                        },
                        plans: [
                            {
                                link: ''
                            }
                        ]
                    }]
                }
            }));
        });
    });
    searchControl.search('Таллинское 27А');
});

function convert(searchData) {
    console.log(searchData);
    var features = [];
    searchData.data.sites.forEach(function (rawSite) {
        features.push(feature(rawSite));
    });
    console.log({
        type: "FeatureCollection",
        features: features
    });
    return {
        type: "FeatureCollection",
        features: features
    };

}

function feature(rawSite) {
    return {
        type: "Feature",
        id: rawSite.id,
        geometry: rawSite.position.location,
        properties: {
            balloonContent: balloonContent(rawSite)
        },
        options: {
            balloonShadow: false,
            balloonLayout: BalloonLayout,
            balloonContentLayout: BalloonContentLayout,
            balloonPanelMaxMapArea: 0
            // Не скрываем иконку при открытом балуне.
            // hideIconOnBalloonOpen: false,
            // И дополнительно смещаем балун, для открытия над иконкой.
            // balloonOffset: [3, -40]
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
           '<a class="btn" onclick="submitEdit()">Сохранить</a>' +
           '<a class="btn" onclick="removePlacemark()">Удалить</a>';
}

function link() {
    var link = $('#link').val();
    if (link != null) {
        return link;
    }
    return site.raw.plans[0].link;
}

function sync() {
    //console.log('#img' + site.id);
    //console.log($('#link').val());
    //$('#img' + site.id).attr('src', $('#link').val());
    site.properties.balloonContent = editBalloonContent();
    refresh();
}

function submitEdit() {
    var rawSite = site.raw;
    rawSite.plans[0].link = $('#link').val();
    var add = rawSite.id == '';
    $.ajax({
        url: (add ? '/admin/site/add' : '/admin/site/edit') + '?site=' + JSON.stringify(rawSite),
        type: 'GET',
        success: function (id) {
            alert('Загружено успешно!');
            site.properties.balloonContent = balloonContent(rawSite);
            if (add) {
                console.log(id);
                objectManager.objects.remove(objectManager.objects.getById(''));
                site.id = id;
                site.raw.id = id;
                objectManager.objects.add(site);
            }
            refresh();
        },
        error: function () {
            alert('Ошибка!')
        }
    });
    refresh();
}
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8" name="viewport"
          content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>HyperNavi</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

    <link href="/web/pages/carousel.css" rel="stylesheet"/>

    <style>
        body {
            background-color: RGB(241, 242, 245)
        }
    </style>
</head>
<body style="padding-top: 70px;" data-spy="scroll" data-target=".navbar">

<div class="navbar navbar-inverse navbar-fixed-top shadow" style="height: 70px;">

    <div class="container" style="background-color: RGB(183, 207, 217);">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>

            <div class="brand-centered" style="background-color: RGB(183, 207, 217); padding-left: 40px;">
                <a class="navbar-brand" href="/">
                    <img src="/web/img/logo.png" style="margin-right: 10px; padding: 0; width: 60px; margin-top: -7px;">
                    <font color="white" style="font-weight: bold;">HyperNavi</font>
                </a>
                <div class="collapse navbar-collapse">
                    <ul class="nav navbar-nav">
                        <li><a class="navbar-brand" href="#usecases"><font color="white">Usecases</font></a></li>
                        <li><a class="navbar-brand" href="#features"><font color="white">Features</font></a></li>
                        <li><a class="navbar-brand" href="#how-it-works"><font color="white">How it works</font></a></li>
                    </ul>
                </div>
            </div>

        </div>
    </div>

</div>


<div class="container">

    <h1 align="center">See your land position on visitor-adapted maps!</h1>

    <div class="row">
        <div class="col-lg-1"></div>
        <div class="col-lg-5">
            <img src="/web/img/man.png" width="85%" height="85%" style="margin-top: 60px;">
        </div>
        <div class="col-lg-2"></div>
        <div class="col-lg-3">
            <img src="/web/img/example.gif" width="115%" height="115%" style="margin-top: 40px; box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)">
        </div>
        <div class="col-lg-1"></div>
    </div>

    <hr class="featurette-divider">

    <p align="center">
        <a target="_blank" href="https://telegram.me/HyperNaviBot" class="btn btn-success btn-lg">
            Get @HyperNaviBot
        </a>
    </p>

    <hr class="featurette-divider">

    <div id="usecases" class="row featurette">
        <div class="col-md-7">
            <h2 class="featurette-heading">Your land positionon hand-made map<span class="text-muted"></span></h2>
            <p class="lead">Send your location to bot. He will return nearest map with your position on it</p>
        </div>
        <div class="col-md-5">
            <img class="featurette-image img-responsive center-block shadow" src="/web/img/example.jpg">
        </div>
    </div>

    <hr class="featurette-divider">

    <div class="row featurette">
        <div class="col-md-7 col-md-push-5">
            <h2 class="featurette-heading">Where are you?<span class="text-muted"></span>
            </h2>
            <p class="lead">Your friends will easily find you in huge popular park.
                Use inline mode to send picture with your location to friend</p>
        </div>
        <div class="col-md-5 col-md-pull-7">
            <img class="featurette-image img-responsive center-block shadow" src="/web/img/inline.jpg">
        </div>
    </div>

    <hr class="featurette-divider">

    <div class="row featurette">
        <div class="col-md-7">
            <h2 class="featurette-heading">Ski resort navigation</h2>
            <p class="lead">Popular European ski resorts are very confusing.
                HyperNavi will show your position on human-adapted ski pistes map. You very simple find path to desired ski lift</p>
        </div>
        <div class="col-md-5">
            <img class="featurette-image img-responsive center-block shadow" src="/web/img/zermatt-pistes.png">
        </div>
    </div>

    <hr class="featurette-divider">

    <div id="features" class="row featurette">
        <div class="col-md-7">
            <h2 class="featurette-heading">Better than paper map</h2>
            <p class="lead">
                Paper map with navigation is better than just paper map
            </p>
        </div>
        <div class="col-md-5">
            <img class="featurette-image img-responsive center-block shadow" src="http://hypernavi.net/draw/location?x=320&y=320&link=http://hypernavi.net/img/i199/1211/36/cf03e5615cbe.jpg">
        </div>
    </div>

    <hr class="featurette-divider">

    <div class="row featurette">
        <div class="col-md-7 col-md-push-5">
            <h2 class="featurette-heading">No more park plan photos</h2>
            <p class="lead">All park schemes are already in your smartphone</p>
        </div>
        <div class="col-md-5 col-md-pull-7">
            <img class="featurette-image img-responsive center-block shadow" src="http://s019.radikal.ru/i600/1612/3d/d33f76e54a4e.jpg">
        </div>
    </div>

    <hr class="featurette-divider">

    <div class="row featurette">
        <div class="col-md-7">
            <h2 class="featurette-heading">Most popular places</h2>
            <p class="lead">HyperNavi database contains most visited places around The World.
                Therefore, the bot most likely will be useful for you.</p>
        </div>
        <div class="col-md-5">
            <img class="featurette-image img-responsive center-block shadow" src="http://hypernavi.net/draw/location?x=450&y=250&link=http://hypernavi.net/img/travel/images/russia2012d060.jpg">
        </div>
    </div>

    <hr class="featurette-divider">

    <div class="row featurette">
        <div class="col-md-7 col-md-push-5">
            <h2 class="featurette-heading">Supported places</h2>
            <p class="lead">Our database is constantly updated.
                See all places, currently supported by HyperNavi <a target="_blank" href="/map">hypernavi.net/map</a></p>
        </div>
        <div class="col-md-5 col-md-pull-7">
            <img class="featurette-image img-responsive center-block shadow" src="/web/img/map.png">
        </div>
    </div>

    <hr class="featurette-divider">

    <div id="how-it-works" class="row featurette">
        <div class="col-md-7">
            <h2 class="featurette-heading">How HyperNavi works?<span class="text-muted">machine learning</span>
            </h2>
            <p class="lead">We assosiate points at image with points at Google/Yandex maps. So, we understand image location for some geopoints.
                After that, we use machine learning regression algorithms to compute location on image by other geopoints.
            </p>
            <img class="featurette-image img-responsive center-block shadow" src="http://clipartix.com/wp-content/uploads/2016/06/Map-location-clip-art-at-clker-vector-clip-art.png">
        </div>
        <div class="col-md-5">
            <img class="featurette-image img-responsive center-block shadow" src="/web/img/ml.png">
        </div>
    </div>

    <hr class="featurette-divider">

    <div class="row featurette">
        <div class="col-md-7 col-md-push-5">
            <h2 class="featurette-heading">Manually marked maps<br><span class="text-muted">no magic, just hardcore</span>
            </h2>
            <p class="lead">Each map marked by human, so, your result location is very accurate</p>
        </div>
        <div class="col-md-5 col-md-pull-7">
            <img class="featurette-image img-responsive center-block shadow" src="/web/img/mapping.png">
        </div>
    </div>

    <hr class="featurette-divider">

    <div class="row featurette">
        <div class="col-md-7">
            <h2 class="featurette-heading">Open source<br><span class="text-muted">like Telegram</span></h2>
            <br>
            <p class="lead">See sources at GitHub
                <a class="btn btn-default bth-lg" href="https://github.com/amosov-f/hypernavi">
                    <img src="https://github.com/fluidicon.png" height="20" width="20" alt="Logo GitHub">
                    <b> Watch</b>
                </a>
            </p>
        </div>
        <div class="col-md-5">
            <img class="featurette-image img-responsive center-block" src="http://popularcoin.com/images/github-6-xxl.png">
        </div>
    </div>

</div>

</body>
</html>
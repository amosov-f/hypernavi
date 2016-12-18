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
<body>

<nav class="navbar navbar-inverse navbar-static-top shadow" style="height: 70px;">

    <div class="container" style="background-color: RGB(183, 207, 217);">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>

            <div class="brand-centered" style="background-color: RGB(183, 207, 217);">
            <a class="navbar-brand" href="/">
                    <img src="/web/img/logo.png" style="margin-right: 10px; padding: 0; width: 60px; margin-top: -7px;" alt="Brand"><font color="white" style="font-weight: bold;">HyperNavi</font>
                </a>
            </div>


        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
            </ul>
        </div>
    </div>

</nav>


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

    <div class="row featurette">
        <div class="col-md-7">
            <h2 class="featurette-heading">Your land positionon hand-made map<span class="text-muted"></span></h2>
            <p class="lead">Send your location to bot. He will return nearest map with your position on it</p>
        </div>
        <div class="col-md-5">
            <img class="featurette-image img-responsive center-block shadow" src="http://s009.radikal.ru/i307/1611/9a/8cb37a0b8c52.jpg">
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
            <img class="featurette-image img-responsive center-block shadow" src="/web/img/inline.png">
        </div>
    </div>

    <hr class="featurette-divider">

    <div class="row featurette">
        <div class="col-md-7">
            <h2 class="featurette-heading">Ski resort navigation</h2>
            <p class="lead">Popular European ski resorts are very confusing.
                HyperNavi will show your position on human-adapted ski pistes map. You very simple find path to desired ski lift</p>
            <img class="featurette-image img-responsive center-block shadow" src="/web/img/zermatt-zoom.jpg">
        </div>
        <div class="col-md-5">
            <img class="featurette-image img-responsive center-block shadow" src="/web/img/zermatt.jpg">
        </div>
    </div>

    <hr class="featurette-divider">

    <p align="center">
        <a href="/web/pages/features.html" class="btn btn-info btn-lg">
            More features
        </a>
        <a href="/web/pages/how-it-works.html" class="btn btn-info btn-lg">
            How it works?
        </a>
    </p>

</div>

</body>
</html>
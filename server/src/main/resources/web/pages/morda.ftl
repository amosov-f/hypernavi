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
</head>
<body style="padding-top: 70px; position: relative; background-color: RGB(241, 242, 245)" data-spy="scroll" data-target=".navbar" data-offset="225">

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
                        <li><a class="navbar-brand" href="#usecases">Usecases</a></li>
                        <li><a class="navbar-brand" href="#how-to-use-bot">How to use bot</a></li>
                        <li><a class="navbar-brand" href="#features">Features</a></li>
                        <li><a class="navbar-brand" href="#how-bot-works">How bot works</a></li>
                    </ul>
                </div>
            </div>

        </div>
    </div>

</div>

<div class="row" style="margin-left: 0; margin-right: 0">
    <div class="col-lg-8">
        <img src="/web/img/man.png" style="width: 100%; margin-top: 10px;">
    </div>
    <div class="col-lg-4">
        <h3 style="margin-top: 50px;">Find out your land position on visitor-adapted maps!</h3>
        <img src="/web/img/disneyland.png" width="90%" style="margin-top: 30px;" class="shadow"/>
        <a target="_blank" href="https://telegram.me/HyperNaviBot" class="btn btn-default btn-lg get-hypernavi-bot"
           style="margin-top: 100px;">
            GET @HyperNaviBot
        </a>
        <p style="margin-top: 20px;">Never get lost yourself and your loved ones in the popular places</p>
    </div>
</div>

<div id="usecases" class="carousel slide" data-ride="carousel">
    <!-- Indicators -->
    <ol class="carousel-indicators">
        <li data-target="#usecases" data-slide-to="0" class="active"></li>
        <li data-target="#usecases" data-slide-to="1"></li>
    </ol>

    <!-- Wrapper for slides -->
    <div class="carousel-inner" role="listbox">
        <div class="item active">
            <div class="col-lg-7 flex-center">
                <img class="shadow" src="/web/img/gorillas.png" style="margin-top: 100px;">
            </div>
            <div class="col-lg-3">
                <p style="margin-top: 100px;">Use the <a href="https://core.telegram.org/bots/inline">inline mode</a> to
                    find your friends each other</p>
            </div>
        </div>

        <div class="item">
            <div class="col-lg-7 flex-center">
                <img class="shadow" src="/web/img/pistes.png" style="margin-top: 100px;">
            </div>
            <div class="col-lg-3">
                <p style="margin-top: 100px;">Find out your position on human-adapted ski pistes map to get the path to
                    the desired <b>ski lift</b></p>
            </div>
        </div>
    </div>

    <!-- Left and right controls -->
    <a class="left carousel-control" href="#usecases" role="button" data-slide="prev">
        <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
        <span class="sr-only">Previous</span>
    </a>
    <a class="right carousel-control" href="#usecases" role="button" data-slide="next">
        <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
        <span class="sr-only">Next</span>
    </a>
</div>

<div id="how-to-use-bot"
     style="background-color: RGB(200, 211, 214); padding-top: 60px; padding-bottom: 60px;">
    <div class="container">
        <div class="row featurette container-fluid">
            <div class="col-md-6 col-md-push-6">
                <h2 class="featurette-heading">How to use bot?</h2>
                <ul>
                    <li><p style="margin-top: 30px;">Press "send my location"</p></li>
                    <li><p style="margin-top: 30px;">Get the nearest place map</p></li>
                    <li><p style="margin-top: 30px;">See your land position</p></li>
                </ul>
            </div>
            <div class="col-md-6 col-md-pull-6">
                <img class="featurette-image img-responsive center-block shadow" src="/web/img/example.gif" width="50%"
                     height="50%">
            </div>
        </div>
    </div>
</div>

<div id="features" class="carousel slide" data-ride="carousel">
    <!-- Indicators -->
    <ol class="carousel-indicators">
        <li data-target="#features" data-slide-to="0" class="active"></li>
        <li data-target="#features" data-slide-to="1"></li>
        <li data-target="#features" data-slide-to="2"></li>
        <li data-target="#features" data-slide-to="3"></li>
    </ol>

    <!-- Wrapper for slides -->
    <div class="carousel-inner" role="listbox">
        <div class="item active">
            <div class="col-lg-7 flex-center">
                <img class="shadow" height="300px" style="margin-top: 100px;"
                     src="http://hypernavi.net/draw/location?x=320&y=320&link=http://hypernavi.net/img/i199/1211/36/cf03e5615cbe.jpg">
            </div>
            <div class="col-lg-3">
                <h2 style="margin-top: 100px;">Better than paper map</h2>
                <p>Paper map with navigation is better than just paper map</p>
            </div>
        </div>

        <div class="item">
            <div class="col-lg-7 flex-center">
                <img class="shadow" height="300px" src="http://s019.radikal.ru/i600/1612/3d/d33f76e54a4e.jpg"
                     style="margin-top: 100px;">
            </div>
            <div class="col-lg-3">
                <h2 style="margin-top: 100px;">No more park plan photos</h2>
                <p class="lead">All park schemes are already in your smartphone</p>
            </div>
        </div>

        <div class="item">
            <div class="col-lg-6 flex-center">
                <img class="shadow" height="400px"
                     src="http://hypernavi.net/draw/location?x=450&y=250&link=http://hypernavi.net/img/travel/images/russia2012d060.jpg"
                     style="margin-top: 20px;">
            </div>
            <div class="col-lg-4">
                <h2 style="margin-top: 100px;">Most popular places</h2>
                <p class="lead">HyperNavi database contains most visited places around The World.
                    Therefore, the bot most likely will be useful for you.</p>
            </div>
        </div>

        <div class="item">
            <div class="col-lg-6 flex-center">
                <img class="shadow" height="400px" src="/web/img/map.png" style="margin-top: 20px;">
            </div>
            <div class="col-lg-4">
                <h2 style="margin-top: 100px;">Supported places</h2>
                <p class="lead">Our database is constantly updated.
                    See all places, currently supported by HyperNavi
                    <a target="_blank" href="/map">hypernavi.net/map</a>
                </p>
            </div>
        </div>
    </div>

    <!-- Left and right controls -->
    <a class="left carousel-control" href="#features" role="button" data-slide="prev">
        <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
        <span class="sr-only">Previous</span>
    </a>
    <a class="right carousel-control" href="#features" role="button" data-slide="next">
        <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
        <span class="sr-only">Next</span>
    </a>
</div>

<div id="how-bot-works"
     style="background-color: #333333; padding-top: 60px; padding-bottom: 60px; color: white;">
    <div class="container">
        <div class="row featurette container-fluid">
            <div class="col-md-6">
                <h4 class="featurette-heading">Machine learning</h4>
                <p>We assosiate points at picture with geopoints at Google/Yandex maps</p>
                <img src="/web/img/admin.png" width="100%">
                <p>
                    After that, we use machine learning regression algorithms to compute location on picture for any geopoint
                    <img src="http://www.freeiconspng.com/uploads/yellow-light-bulb-png-image-16.png" width="30">
                </p>

            </div>
            <div class="col-md-6">
                <img src="/web/img/ml.png" width="85.7%">
                <p style="font-style: italic; color: RGB(125, 125, 125);">
                    Each map marked by human, so, your result location is very accurate
                </p>
            </div>
        </div>
    </div>
</div>

<div style="padding-top: 30px; padding-bottom: 20px; background-color: RGB(57, 71, 76)">
    <div class="container">
        <footer>
            <p class="pull-right"><a href="mailto:amosov.f@mail.ru" class="btn btn-lg">Contact us via email</a></p>
            <p>
                <a target="_blank" class="btn btn-default btn-lg" href="https://github.com/amosov-f/hypernavi">
                    <img src="/web/img/github.png" width="24px">
                    Watch on GitHub
                </a>
                &nbsp;
                <a target="_blank" href="https://telegram.me/HyperNaviBot" class="btn btn-default btn-lg get-hypernavi-bot">
                    GET @HyperNaviBot
                </a>
            </p>

        </footer>
    </div>
</div>

</body>
</html>
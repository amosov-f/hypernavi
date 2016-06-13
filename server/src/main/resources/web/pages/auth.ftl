<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">

    <title>Auth</title>

    <!-- Put this script tag to the <head> of your page -->
    <script type="text/javascript" src="//auth.vk.com/js/api/openapi.js?121"></script>

    <script type="text/javascript">
        VK.init({apiId: 5102874});
    </script>

    <script type="text/javascript">
        VK.Widgets.Auth("vk_auth", {width: "200px", authUrl: "/setauth?url=${url}"});
    </script>
</head>
<body>
	<!-- Put this div tag to the place, where Auth block will be -->
	<div id="vk_auth"></div>
</body>
</html>
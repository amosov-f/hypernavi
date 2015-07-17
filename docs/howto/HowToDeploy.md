# Как обновить сервер hypernavi.cloudapp.net

Для начала нужно зайти на него под пользователем hypernavi

`ssh hypernavi@hypernavi.cloudapp.net`

Затем, выполнить следующую команду

`sudo sh /hol/arkanavt/hypernavi/run-server.sh`

Начнется maven-сборка последней версии проекта. Если тесты пройдут и сборка окажется успешной, то Вы увидите строчку 
"Starting server...", и через некоторое время сервер обновится. Проверить, поднялся ли сервер, можно дернув следующий урл,

[http://hypernavi.cloudapp.net/category?text=носки](http://hypernavi.cloudapp.net/category?text=носки)

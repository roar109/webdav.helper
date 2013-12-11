webdav.helper
=============

Simple java app that help you to upload resources through webdav API, using sardine framework.

##### How it works?

Just copy your resouces to "resources" directory and modify the props.properties to  target your webdav server IP, user, pass and context path.

And type on command line "./install.sh" or "install.bat", or if you have apache ant installed on your computer just call "ant".

The available targets are:
* compile
* run
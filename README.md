[![Java CI with Maven](https://github.com/k-morozov/goodok/actions/workflows/maven.yml/badge.svg)](https://github.com/k-morozov/goodok/actions/workflows/maven.yml)
[![Docker Image CI](https://github.com/k-morozov/goodok/actions/workflows/docker-image.yml/badge.svg)](https://github.com/k-morozov/goodok/actions/workflows/docker-image.yml)
[![codecov](https://codecov.io/gh/k-morozov/goodok/branch/master/graph/badge.svg?token=CLW2KM8VFM)](https://codecov.io/gh/k-morozov/goodok)

##How to start?

###requirments:
- docker

###run:
```aidl
docker build -t goodok-server .
docker run -it -p 7777:8018  goodok-server
```
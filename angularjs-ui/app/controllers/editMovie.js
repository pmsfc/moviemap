'use strict';

var mmodule = angular.module('myApp.controllers');

mmodule.controller('EditCtrl', ['$scope', 'S_PersonalData', '$location', '$document', '$http', '$sce', '$window', 'NgMap', '$routeParams', 'toastr', '$filter', function ($scope, S_PersonalData, $location, $document, $http, $sce, $window, NgMap, $routeParams, toastr, $filter) {

    if (S_PersonalData.s_getPersonalData().isLogin != 'yes') {
        $location.path("/login");
    }

    $scope.added = false;
    $scope.wsUrl = "localhost:8084";
    $scope.movieID = $routeParams.param1;
    $scope.movie = {name: "LOADING..."};
    $scope.poster = "images/loading.jpg";

    // initiliaze
    initialize();

    function initialize() {
        getMovieData();
        getLocations();
    }

    function getMovieData() {
        var query2 = "http://" + $scope.wsUrl + "/ws/movie/" + $scope.movieID;
        $http.jsonp($sce.trustAsResourceUrl(query2)).then(
            function onSuccess(response) {
                $scope.movie = response.data;
                $scope.date = new Date($scope.movie.date);
                $scope.poster = "https://image.tmdb.org/t/p/w500/" + $scope.movie.picture;
                $scope.desc = response.data.overview;
                $scope.movieName = response.data.name;
                getAllCategories();
                getAllCountries();
                if (!$scope.$$phase) {
                    $scope.$apply(); // just flush it ...
                }
            },
            function onError(data) {
                console.log("ERROR");
            });
    }

    function getAllCategories() {
        var query2 = "http://" + $scope.wsUrl + "/ws/category/";
        $http.jsonp($sce.trustAsResourceUrl(query2)).then(
            function onSuccess(response) {
                var rawCategories = response.data;
                var i = 0, j = 0;
                $scope.cstatuses = [];
                $scope.categorySelected = [];
                rawCategories.forEach(function (category) {
                    $scope.cstatuses[i] = {value: i, text: category.name}
                    $scope.movie.category.forEach(function (movieCategory) {
                        if (movieCategory == category.name) {
                            $scope.categorySelected[j] = i;
                            j++;
                        }
                    });
                    i++;
                });
                $scope.showCategoryStatus();

            }, function onError(data) {
                console.log("ERROR");
            });
    }

    function getAllCountries() {
        var query2 = "http://" + $scope.wsUrl + "/ws/country/";
        $http.jsonp($sce.trustAsResourceUrl(query2)).then(
            function onSuccess(response) {
                var rawCountries = response.data;
                var i = 0, j = 0;
                $scope.countryStatuses = [];
                $scope.countrySelected = [];
                rawCountries.forEach(function (country) {
                    $scope.countryStatuses[i] = {value: i, text: country.name}
                    $scope.movie.country.forEach(function (movieCountry) {
                        if (movieCountry == country.name) {
                            $scope.countrySelected[j] = i;
                            j++;
                        }
                    });
                    i++;
                });
                $scope.showCountryStatus();

            }, function onError(data) {
                console.log("ERROR");
            });
    }

    function getLocations() {
        $scope.locations = null;
        var query2 = "http://" + $scope.wsUrl + "/ws/location/movie/" + $scope.movieID;
        $http.jsonp($sce.trustAsResourceUrl(query2)).then(
            function onSuccess(response) {
                $scope.locations = response.data;
            }, function onError(data) {
                console.log("ERROR");
            });
    }

    $scope.addNewMarker = function (address) {
        if (address === "" || typeof address === 'undefined')
            return;

        var geocoder = new google.maps.Geocoder;
        geocoder.geocode({'address': address}, function (results, status) {
            if (status === 'OK') {
                var lat = results[0].geometry.location.lat();
                var lng = results[0].geometry.location.lng();
                var streetnoples = address.replace(/ /g, '_');
                var streetfinal = streetnoples.replace(/[^\w\s]/gi, '');
                var parameter = JSON.stringify({
                    name: streetfinal,
                    coordinates: lat + "," + lng,
                    movies: $scope.movie.name
                });
                console.log(parameter);
                var url = "http://" + $scope.wsUrl + "/ws/location/";
                $http.post($sce.trustAsResourceUrl(url), parameter).then(
                    function onSuccess(data) {
                        $scope.address = "";
                        getLocations();
                        getMovieData();
                    }, function onError(data) {
                        console.log("Empty response");
                    });
            } else {
                alert('Geocode was not successful for the following reason: ' + status);
            }
        });
    }

    $scope.markerclick2 = function ($event, $localname) {
        if (confirm('Do you want to delete marker?')) {
            deletebyname($localname);
        }
    }

    function deletebyname(name) {
        var url = "http://" + $scope.wsUrl + "/ws/location/movie/" + $scope.movieID + "/location/" + name;
        $http.delete($sce.trustAsResourceUrl(url)).then(
            function onSuccess(data) {
                //all good here!
                getLocations();
                getMovieData();
            }, function onError(data) {
                console.log("Empty response");
            });
    }

    $scope.getCurrentLocationNew = function (e) {
        var myLatLng = e.latLng;
        var geocoder = new google.maps.Geocoder;
        geocoder.geocode({'location': myLatLng}, function (results, status) {
            if (status === 'OK') {
                if (results[1]) {
                    var street = results[1].formatted_address;
                    $scope.address = street;
                    console.log(street);
                    $scope.$apply();
                }
            }
        });
    }

    $scope.getCurrentLocation = function (e, local) {
        var myLatLng = e.latLng;
        var lat = myLatLng.lat();
        var lng = myLatLng.lng();
        var geocoder = new google.maps.Geocoder;
        geocoder.geocode({'location': myLatLng}, function (results, status) {
            if (status === 'OK') {
                if (results[1]) {
                    var street = results[1].formatted_address;
                    $scope.commentData = "";
                    var streetnoples = street.replace(/ /g, '_');
                    var streetfinal = streetnoples.replace(/[^\w\s]/gi, '');
                    var parameter = JSON.stringify({
                        name: streetfinal,
                        coordinates: lat + "," + lng,
                        movies: local.movies
                    });
                    console.log(parameter);
                    var url = "http://" + $scope.wsUrl + "/ws/location/";
                    $http.post($sce.trustAsResourceUrl(url), parameter).then(
                        function onSuccess(data) {
                            deletebyname(local.name);
                        }, function onError(data) {
                            console.log("Empty response");
                        });
                }
            }
        });
    }

    $scope.opened = {};
    $scope.open = function ($event, elementOpened) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.opened[elementOpened] = !$scope.opened[elementOpened];
    };

    $scope.showCategoryStatus = function () {
        $scope.selected = [];
        angular.forEach($scope.cstatuses, function (s) {
            if ($scope.categorySelected.indexOf(s.value) >= 0) {
                $scope.selected.push(s.text.replace(/_/g, " "));
            }
        });
        return $scope.selected.length ? $scope.selected.join(', ') : 'Not set';
    };

    $scope.showCountryStatus = function () {
        $scope.selectedC = [];
        angular.forEach($scope.countryStatuses, function (s2) {
            if ($scope.countrySelected.indexOf(s2.value) >= 0) {
                $scope.selectedC.push(s2.text.replace(/_/g, " "));
            }

        });
        return $scope.selectedC.length ? $scope.selectedC.join(', ') : 'Not set';

    };

    $scope.map = NgMap.getMap();
    NgMap.getMap().then(function (map) { //Create the instance of map
        $scope.markers = map.markers;
    });

    $scope.smubitnewinfo = function () {
        toastr.success('Changing Movie please wait...' + $scope.movie.name.replace(/_/g, " "), 'Ediding Page');

        var categoriesToPersist = [], i = 0;
        $scope.categorySelected.forEach(function (index) {
            categoriesToPersist[i] = $scope.cstatuses[index].text;
            i++;
        });

        var countriesToPersist = [], i = 0;
        $scope.countrySelected.forEach(function (index) {
            countriesToPersist[i] = $scope.countryStatuses[index].text;
            i++;
        });

        var movieToPersist = JSON.stringify({
            "name": $scope.movieName,
            "id": $scope.movie.id,
            "overview": $scope.desc,
            "page": $scope.movie.page,
            "picture": $scope.movie.picture,
            "actors": $scope.movie.actors,
            "date": $scope.date.toJSON().slice(0,10),
            "category": categoriesToPersist,
            "duration": $scope.movie.duration,
            "crew": [],
            "keyword": $scope.movie.keyword,
            "rating": $scope.movie.rating,
            "country": countriesToPersist,
            "location": $scope.movie.location,
            "popularity": $scope.movie.popularity,
            "imdbID": $scope.movie.imdbID
        });

        var url = "http://" + $scope.wsUrl + "/ws/movie/";
        $http.put($sce.trustAsResourceUrl(url), movieToPersist).then(
            function onSuccess(data) {
                toastr.success('DONE!', 'Ediding Page');
            }, function onError(data) {
                alert("ERROR");
            });

    }

}]); //end of controller

mmodule.filter('clean', function () {
    return function (text) {
        return String(text).replace(/_/g, " ");
    };
});


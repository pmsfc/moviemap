'use strict';

var mmodule = angular.module('myApp.controllers');

// Home controller
mmodule.controller('HomeCtrl', ['$scope', 'S_PersonalData', '$location', '$document', '$http', '$sce', '$window', 'NgMap', '$interval', '$q','toastr', function ($scope, S_PersonalData, $location, $document, $http, $sce, $window, NgMap, $interval, $q,toastr) {

    if (S_PersonalData.s_getPersonalData().isLogin != 'yes') {
        $location.path("/login");
    }
    /*  persistUserIfNotPersisted(S_PersonalData.s_getPersonalData().name, S_PersonalData.s_getPersonalData().email,
     S_PersonalData.s_getPersonalData().profile);*/

    $scope.username = S_PersonalData.s_getPersonalData().name;
    $scope.userimg = S_PersonalData.s_getPersonalData().profile;
    $scope.teste = "testar";
    var promise;

    if ($window.innerHeight < 750) {
        $scope.length = 9;
    } else {
        $scope.length = 11;
    }

    $scope.wsUrl = "moviemap.win:8080";
    $scope.max = 7;
    $scope.isReadonly = false;
    $scope.movieDetails = false;
    $scope.map = true;
    $scope.cats = true;
    $scope.sch = true;
    $scope.similar = false;

    $scope.hoveringOver = function (value) {
        $scope.overStar = value;
        $scope.percent = 100 * (value / $scope.max);
    };

    $scope.ratingStates = [
        {stateOn: 'glyphicon-ok-sign', stateOff: 'glyphicon-ok-circle'},
        {stateOn: 'glyphicon-star', stateOff: 'glyphicon-star-empty'},
        {stateOn: 'glyphicon-heart', stateOff: 'glyphicon-ban-circle'},
        {stateOn: 'glyphicon-heart'},
        {stateOff: 'glyphicon-off'}
    ];

    NgMap.getMap().then(function (map) {
        var mapStyle = [
            {
                featureType: "administrative",
                elementType: "geometry",
                stylers: [
                    {visibility: "off"}
                ]
            }
        ];

        var styledMap = new google.maps.StyledMapType(mapStyle);
        map.mapTypes.set('myCustomMap', styledMap);
        map.setMapTypeId('myCustomMap');

    });

    var categoriesSelected = [];
    $scope.pageNum = 1;
    var latestP = "";
    $scope.currentNavItem = 'movies'; //pagination
    $scope.googleMapsUrl = "key=AIzaSyBsI7-D92h-5LXK9EdzfWc8K6EX6XiRD4U";
    getCategories();

    if (latestP === "") {
        updateMovies("http://" + $scope.wsUrl + "/ws/movie/");
    } else {
        updateMovies(latestP);

    }

    $scope.markerclick = function ($event, movie) {
        console.log(movie);
        var query2 = "http://" + $scope.wsUrl + "/ws/movie/" + movie.id;
        $http.jsonp($sce.trustAsResourceUrl(query2)).then(
            function onSuccess(response) {
                movie = response.data;
                $scope.movie = movie;
                $scope.movie.rating2 = Math.round((($scope.movie.rating * 7) / 10) * 10) / 10;
                $scope.map = false;
                $scope.sch = false;
                $scope.cats = false;
                $scope.movieinfo = true;
                $scope.comentbox = false;
                $scope.trailer = true;
                var res = movie.date.split("-");
                getVideoByMovieName(movie.name + res[0]);
            }, function onError(data) {
                console.log("ERROR");
                // called asynchronously if an error occurs
                // or server returns response with an error status.

            });
    };
    $scope.recommendedInfoFeedbacks = function ($movieName) {
        $scope.comments = [];
        $scope.movieNameTO = $movieName;
        updateComments($scope.movieNameTO);
        $scope.trailer = false;
        $scope.comentbox = true;
        $scope.start();
    };

    $scope.deleteComment = function ($commentID) {
        var url = "http://" + $scope.wsUrl + "/ws/comment/" + $commentID;
        $http.delete($sce.trustAsResourceUrl(url)).then(
            function onSuccess(data) {
                //console.log(data);
                updateComments($scope.movieNameTO);
            }, function onError(data) {
                updateComments($scope.movieNameTO);
                console.log("Empty response");
            });
    };

    function updateComments($movieName) {
        var query = 'http://' + $scope.wsUrl + '/ws/comment/movie/' + $movieName; // baseURL for API
        $http.jsonp($sce.trustAsResourceUrl(query)).then(
            function onSuccess(data) {
                if (data[0] !== null) {
                    $scope.comments = data.data;
                    console.log("Fetched new comments");
                } else
                    $scope.comments = [];

            }, function onError(data) {
                $scope.comments = [];
            });
    }

    $scope.start = function () {
        // stops any running interval to avoid two intervals running at the same time
        $scope.stop();
        // store the interval promise
        promise = $interval(function () {
            updateComments($scope.movieNameTO);
        }, 3000);
    }

    // stops the interval
    $scope.stop = function () {
        $interval.cancel(promise);
    };

    $scope.$on('$destroy', function () {
        $scope.stop();
    });

    $scope.closeInfo = function () {
        console.log("changed to map view");
        $scope.stop();
        $scope.map = true;
        $scope.sch = true;
        $scope.cats = true;
        $scope.movieinfo = false;
    };

    $scope.recommendedInfo = function () {
        $scope.stop();
        $scope.map = true;
        $scope.sch = true;
        $scope.movieinfo = false;

        $scope.movie.category.forEach(function (key) {
            categoriesSelected.push(key);
        });
        $scope.pesquisar();
        $scope.similar = true;
        toastr.info($scope.movie.name.replace(/_/g, " "), 'Showing similar movies to:');

    };
    $scope.detailsInfo = function () {
        $scope.stop();
        $scope.movieinfo = true;
        $scope.trailer = true;
        $scope.comentbox = false;
    };

    $scope.pesquisar = function (pesquisa) {
        if (typeof pesquisa !== "undefined" && pesquisa.length > 1)
        pesquisa = pesquisa.replace(/\s+/g, '_');

        if (typeof pesquisa !== "undefined" && pesquisa[0] == '#') {
            console.log("#")
            $scope.cats = false;
            return updateMovies("http://" + $scope.wsUrl + "/ws/movie/keyword/" + pesquisa.substring(1));

        } else {
            $scope.cats = true;
            $scope.pageNum = 1;
            var url;
            if (typeof pesquisa === 'undefined') {
                pesquisa = "";
            }

            if (categoriesSelected.length !== 0) {
                if (pesquisa !== "")
                    url = "http://" + $scope.wsUrl + "/ws/movie/" + pesquisa + "/category/";
                else
                    url = "http://" + $scope.wsUrl + "/ws/movie/category/";
                categoriesSelected.forEach(function (key) {
                    url += key + ",";
                });
                url = url.slice(0, -1);
                console.log(url);
            }
            else if (categoriesSelected.length === 0 && pesquisa === "")
                url = "http://" + $scope.wsUrl + "/ws/movie/";
            else
                url = "http://" + $scope.wsUrl + "/ws/movie/name/" + pesquisa;
            latestP = url;

            if( $scope.similar == true || pesquisa == ""){
                categoriesSelected = [];
                $scope.similar == false;
            }
            return updateMovies(url, pesquisa);
        }
    };

    function updateMovies(url) {
        var deferred = $q.defer();
        var query = url; // baseURL for API
        query += '?page=' + $scope.pageNum;
        query += '&perpage=' + $scope.length;

        $http.jsonp($sce.trustAsResourceUrl(query)).then(
            function onSuccess(data) {
                $scope.completing = true;
                $scope.movies = data.data;
                deferred.resolve(data.data);
                if (data.data[0] === null) {
                    $scope.completing = false;
                    $scope.movies = [];
                }
            }, function onError(data) {
                // called asynchronously if an error occurs
                // or server returns response with an error status.

            });

        return deferred.promise;

    }

    function getCategories() {
        var query = 'http://' + $scope.wsUrl + '/ws/category/'; // baseURL for API
        $http.jsonp($sce.trustAsResourceUrl(query)).then(
            function onSuccess(data) {
                if (data[0] !== null)
                    $scope.roles = data.data;

            }, function onError(data) {
                console.log("Empty response");
            });
    }

    $scope.submitComment = function ($movieName) {
        var timestamp = new Date().getTime();
        var parameter = JSON.stringify({
            id: timestamp,
            movie: $movieName,
            user: S_PersonalData.s_getPersonalData().name,
            text: $scope.commentData
        });
        var url = "http://" + $scope.wsUrl + "/ws/comment/";
        $scope.commentData = "";
        $http.post($sce.trustAsResourceUrl(url), parameter).then(
            function onSuccess(data) {
                //  console.log(data);
                updateComments($movieName);
            }, function onError(data) {
                console.log("Empty response");
            });

    };

    $scope.bandChoosed = function ($search, $status, category) {

        if( $scope.similar == true){
            categoriesSelected = [];
            $scope.similar == false;
        }
        console.log(categoriesSelected);
        $scope.pageNum = 1;
        if ($status) {
            categoriesSelected.push(category);
            $scope.pesquisar($search);
        } else {
            var index = categoriesSelected.indexOf(category);
            if (index > -1) {
                categoriesSelected.splice(index, 1);
            }
            $scope.pesquisar($search);
        }

    };

    $scope.convertRating = function ($number) {
        return Math.round((($number * 7) / 10) * 10) / 10;
    };

    $scope.previous = function () {
        if ($scope.pageNum != 1) {
            $scope.pageNum--;
            if (latestP === "") {
                updateMovies("http://" + $scope.wsUrl + "/ws/movie/");
            } else {
                updateMovies(latestP);

            }
        }
    };

    $scope.next = function () {
        console.log(latestP);
        if ($scope.movies.length == $scope.length) {
            $scope.pageNum++;
            if (latestP === "") {
                updateMovies("http://" + $scope.wsUrl + "/ws/movie/");
            } else {
                updateMovies(latestP);

            }

        }

    };

    $scope.trustSrc = function (src) {
        return $sce.trustAsResourceUrl(src);
    }

    function getVideoByMovieName($name) {
        var videoq = "https://www.googleapis.com/youtube/v3/search?part=id&q=" + $name + "+trailer&key=AIzaSyBsI7-D92h-5LXK9EdzfWc8K6EX6XiRD4U";
        $http.jsonp($sce.trustAsResourceUrl(videoq)).then(
            function onSuccess(response) {
                console.log();
                if (response.data.items.length !== 0) {
                    console.log(response.data.items[0].id.videoId);
                    var vid = response.data.items[0].id.videoId;
                    $scope.trustSrc = function (vid) {
                        return $sce.trustAsResourceUrl(vid);
                    }
                    $scope.videoID = "https://www.youtube.com/embed/" + vid + "?rel=0&amp;showinfo=1";
                } else {
                    $scope.videoID = "http://www.greyhound.co.th/img/video_not_found.jpg";
                }

            }, function onError(data) {
                $scope.videoID = "https://www.youtube.com/embed/vsK8u-Rb3Yc?rel=0&amp;showinfo=1";
                console.log("VIDEO_ERROR");
            });
    }

}]);

mmodule.filter('clean', function () {
    return function (text) {
        if (text.length > 1)
            return String(text).replace(/_/g, " ");
        else
            return text;
    };
});

mmodule.filter('round', function () {
    return function (number) {
        return Math.round(number * 10) / 10;
    };
});

mmodule.filter('year', function () {
    return function (string) {
        var res = string.split("-");
        return res[0];
    };
});






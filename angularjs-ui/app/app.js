'use strict';

var app = angular.module('myApp', [
    'ngRoute',
    'angular-google-auth2',
    'ngMap',
    'ngMaterial',
    'ngAnimate',
    'ngAria',
    'ui.bootstrap',
    'xeditable',
    'toastr',
    'checklist-model',
    'myApp.controllers'           // Newly added home module
]).config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    // Set defualt view of our app to home

    $routeProvider
        .when('/home', {
            templateUrl: "pages/movies.html",
            controller: 'HomeCtrl'
        })
        .when('/login', {
            templateUrl: "pages/login.html",
            controller: 'LoginCtrl'
        })
        .when('/actors', {
            templateUrl: "pages/actors.html",
            controller: 'ActorsCtrl'
        })
        .when('/countries', {
            templateUrl: "pages/countries.html",
            controller: 'CountriesCtrl'
        })
        .when('/editmovie/:param1', {
            templateUrl: "pages/editMovie.html",
            controller: 'EditCtrl'
        })
        .otherwise({
            redirectTo: '/home'
        });
}]);


angular.module('myApp.controllers', ['ngRoute', 'angular-google-auth2', 'ngMap', 'ngMaterial', 'ngAnimate', 'ngAria', 'ui.bootstrap', 'xeditable','toastr','checklist-model']);

app.directive('myEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if(event.which === 13) {
                scope.$apply(function (){
                    scope.$eval(attrs.myEnter);
                });

                event.preventDefault();
            }
        });
    };
});

app.config(function(toastrConfig) {
    angular.extend(toastrConfig, {
        autoDismiss: false,
        containerId: 'toast-container',
        maxOpened: 0,
        newestOnTop: true,
        positionClass: 'toast-bottom-right',
        preventDuplicates: false,
        preventOpenDuplicates: false,
        target: 'body'
    });
});

app.run(function(editableOptions) {
    editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
});

'use strict';

angular.module('myApp.controllers')

.controller('LoginCtrl', ['$scope', 'S_PersonalData', '$location', '$document','$http', '$sce', function ($scope, S_PersonalData, $location,$http, $sce) {

        if(S_PersonalData.s_getPersonalData().isLogin == 'yes'){
            $location.path("/home");
        }else{



        }




    }]);
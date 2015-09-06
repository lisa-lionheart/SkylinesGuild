var theApp = angular.module('theApp', ['ngRoute','citiesServices']);


theApp.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
            when('/list', {
                templateUrl: 'partials/city-list.html',
                controller: 'CityListController'
            }).
            when('/city/:cityId', {
                templateUrl: 'partials/city-info.html',
                controller: 'CityDetailController'
            }).
            otherwise({
                redirectTo: '/list'
            });
    }]);

theApp.controller('UserController',['$scope','$rootScope','$http', function($scope,$rootScope,$http){
    $http.get('/user').then(function(res){
        $scope.loggedIn = (res.data !== "");
        $scope.user = res.data;
    });

    $scope.login = function() {
        window.location.href='/auth/reddit';
    };

    $scope.logout = function() {
        window.location.href='/auth/logout';
    };
}]);

theApp.controller('CityListController',['$scope','City', function($scope, City) {

    document.title = 'Available Cities';

    $scope.cities = City.query();

    $scope.play = function(c) {
        c.$play().then(function(res){
        }, function(err){
            $scope.errorMessage = 'Error launching city: ' + err.statusText;
        });
    }
}]);

theApp.controller('CityDetailController',['$scope','$routeParams','City', function($scope, $routeParams, City) {
    $scope.city = City.get({cityId:$routeParams.cityId});
}]);
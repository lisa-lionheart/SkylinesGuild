var theApp = angular.module('theApp', ['ngRoute','citiesServices']);


theApp.directive('userLink', function(){
      return {
          //restrict: 'E',
          scope: {
            user: '=user'
          },
          templateUrl: 'partials/user-link.html'
      };
});

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

theApp.controller('UserController',['$scope','$rootScope', 'User', function($scope,$rootScope,User){
    $rootScope.currentUser = User.current();

    $scope.login = function() {
        window.location.href='/login';
    };

    $scope.logout = function() {
        window.location.href='/logout';
    };

    $rootScope.getUserName = function(id) {
        return User.get(id).nickName;
    }
}]);

theApp.controller('CityListController',['$scope','$location','City','User', function($scope, $location, City) {

    document.title = 'Available Cities';

    $scope.cities = City.query();

    $scope.download = function(city) {
        window.location.href = city.lastSave.downloadUrl
    }

    $scope.view = function(city) {
        $location.path('/city/'+city.cityId)
    }

    $scope.play = function(c) {
        c.$play().then(function(res){
            //Show some sort of popup
        }, function(err){
            $scope.errorMessage = 'Error launching city: ' + err.statusText;
        });
    }
}]);

theApp.controller('CityDetailController',['$scope','$routeParams','City', function($scope, $routeParams, City) {
    $scope.city = City.get({cityId:$routeParams.cityId});
}]);
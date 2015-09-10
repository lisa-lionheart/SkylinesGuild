var citiesServices = angular.module('citiesServices', ['ngResource']);

citiesServices.factory('City', ['$resource',
    function($resource){
        return $resource('city/:cityId', {
            cityId: '@cityId'
        }, {
            query: {method:'GET', url: 'city/all', isArray:true},
            play: {method:'GET', url: 'city/:cityId/play'}
        });
    }]);


citiesServices.factory('User', ['$resource',
    function($resource){
        return $resource('user/:userId', {
            cityId: '@userId'
        }, {
            get: {method:'GET', url: 'user/:userId'},
            current: {method:'GET', url: 'user'}
        });
    }]);
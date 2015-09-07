var citiesServices = angular.module('citiesServices', ['ngResource']);

citiesServices.factory('City', ['$resource',
    function($resource){
        return $resource('cities/:cityId', {
            cityId: '@cityId'
        }, {
            query: {method:'GET', params:{cityId:'all'}, isArray:true},
            play: {method:'GET', url: 'cities/:cityId/play'}
        });
    }]);
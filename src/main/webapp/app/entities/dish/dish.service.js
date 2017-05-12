(function() {
    'use strict';
    angular
        .module('gastronomeeApp')
        .factory('Dish', Dish);

    Dish.$inject = ['$resource'];

    function Dish ($resource) {
        var resourceUrl =  'api/dishes/:id/:action';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'myDishes': { method: 'GET', isArray: true, params: {action: 'my'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();

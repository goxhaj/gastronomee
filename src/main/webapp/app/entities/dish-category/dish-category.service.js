(function() {
    'use strict';
    angular
        .module('gastronomeeApp')
        .factory('DishCategory', DishCategory);

    DishCategory.$inject = ['$resource'];

    function DishCategory ($resource) {
        var resourceUrl =  'api/dish-categories/:id/:action';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'active': { method: 'GET', isArray: true, params: {action: 'active'}},
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

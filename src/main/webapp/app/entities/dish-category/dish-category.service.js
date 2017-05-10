(function() {
    'use strict';
    angular
        .module('gastronomeeApp')
        .factory('DishCategory', DishCategory);

    DishCategory.$inject = ['$resource'];

    function DishCategory ($resource) {
        var resourceUrl =  'api/dish-categories/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
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

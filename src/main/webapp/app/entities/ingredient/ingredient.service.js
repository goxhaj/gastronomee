(function() {
    'use strict';
    angular
        .module('gastronomeeApp')
        .factory('Ingredient', Ingredient);

    Ingredient.$inject = ['$resource'];

    function Ingredient ($resource) {
        var resourceUrl =  'api/ingredients/:id';

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

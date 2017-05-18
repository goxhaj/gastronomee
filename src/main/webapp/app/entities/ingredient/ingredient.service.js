(function() {
    'use strict';
    angular
        .module('gastronomeeApp')
        .factory('Ingredient', Ingredient);

    Ingredient.$inject = ['$resource'];

    function Ingredient ($resource) {
        var resourceUrl =  'api/ingredients/:id/:action';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'getIngredients': { 
            	method: 'GET', isArray: true, params:{ action : 'name' }
            },
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

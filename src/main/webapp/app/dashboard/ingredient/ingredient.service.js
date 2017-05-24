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
            'my': { method: 'GET', isArray: true, params: {action: 'my'}},
            'getIngredients': { 
            	method: 'GET', isArray: true, params:{ action : 'name' }
            },
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

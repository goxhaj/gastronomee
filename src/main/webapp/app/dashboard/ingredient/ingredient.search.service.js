(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .factory('IngredientSearch', IngredientSearch);

    IngredientSearch.$inject = ['$resource'];

    function IngredientSearch($resource) {
        var resourceUrl =  'api/_search/ingredients/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

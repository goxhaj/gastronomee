(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .factory('DishOrderSearch', DishOrderSearch);

    DishOrderSearch.$inject = ['$resource'];

    function DishOrderSearch($resource) {
        var resourceUrl =  'api/_search/dish-orders/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

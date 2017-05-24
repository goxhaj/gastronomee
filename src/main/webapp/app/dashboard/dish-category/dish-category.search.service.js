(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .factory('DishCategorySearch', DishCategorySearch);

    DishCategorySearch.$inject = ['$resource'];

    function DishCategorySearch($resource) {
        var resourceUrl =  'api/_search/dish-categories/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

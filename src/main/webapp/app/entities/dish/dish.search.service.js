(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .factory('DishSearch', DishSearch);

    DishSearch.$inject = ['$resource'];

    function DishSearch($resource) {
        var resourceUrl =  'api/_search/dishes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

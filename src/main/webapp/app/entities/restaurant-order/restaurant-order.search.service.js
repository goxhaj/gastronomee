(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .factory('RestaurantOrderSearch', RestaurantOrderSearch);

    RestaurantOrderSearch.$inject = ['$resource'];

    function RestaurantOrderSearch($resource) {
        var resourceUrl =  'api/_search/restaurant-orders/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

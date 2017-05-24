(function() {
    'use strict';
    angular
        .module('gastronomeeApp')
        .factory('RestaurantOrder', RestaurantOrder);

    RestaurantOrder.$inject = ['$resource', 'DateUtils'];

    function RestaurantOrder ($resource, DateUtils) {
        var resourceUrl =  'api/restaurant-orders/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.created = DateUtils.convertDateTimeFromServer(data.created);
                        data.updated = DateUtils.convertDateTimeFromServer(data.updated);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();

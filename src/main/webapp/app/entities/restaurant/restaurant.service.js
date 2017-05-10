(function() {
    'use strict';
    angular
        .module('gastronomeeApp')
        .factory('Restaurant', Restaurant);

    Restaurant.$inject = ['$resource'];

    function Restaurant ($resource) {
        var resourceUrl =  'api/restaurants/:id';

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

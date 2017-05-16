(function() {
    'use strict';
    angular
        .module('gastronomeeApp')
        .factory('Restaurant', Restaurant);

    Restaurant.$inject = ['$resource'];

    function Restaurant ($resource) {
        var resourceUrl =  'api/restaurants/:id/:action';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'dishes': { method: 'GET', isArray: true, params: {action: 'dishes'}},
            'my': { method: 'GET', isArray: true, params: {action: 'my'}},
            'menus': { method: 'GET', isArray: true, params: {action: 'menus'}},
            'ratings': { method: 'GET', isArray: true, params: {action: 'ratings'}},
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

(function() {
    'use strict';
    angular
        .module('gastronomeeApp')
        .factory('Menu', Menu);

    Menu.$inject = ['$resource'];

    function Menu ($resource) {
        var resourceUrl =  'api/menus/:id/:action';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'my': { method: 'GET', isArray: true, params: {action: 'my'}},
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

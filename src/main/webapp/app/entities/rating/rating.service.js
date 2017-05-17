(function() {
    'use strict';
    angular
        .module('gastronomeeApp')
        .factory('Rating', Rating);

    Rating.$inject = ['$resource'];

    function Rating ($resource) {
        var resourceUrl =  'api/ratings/:id/:action';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'my': { method: 'GET', isArray: true, params: {action: 'my'}},
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

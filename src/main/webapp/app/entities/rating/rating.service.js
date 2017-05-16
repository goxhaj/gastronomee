(function() {
    'use strict';
    angular
        .module('gastronomeeApp')
        .factory('Rating', Rating);

    Rating.$inject = ['$resource'];

    function Rating ($resource) {
        var resourceUrl =  'api/ratings/:id';

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

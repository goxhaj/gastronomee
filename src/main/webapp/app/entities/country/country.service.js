(function() {
    'use strict';
    angular
        .module('gastronomeeApp')
        .factory('Country', Country);

    Country.$inject = ['$resource'];

    function Country ($resource) {
        var resourceUrl =  'api/countries/:id/:action';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'getCountries': { 
            	method: 'GET', isArray: true, params:{ action : 'name' }
            },
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

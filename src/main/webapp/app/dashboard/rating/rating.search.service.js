(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .factory('RatingSearch', RatingSearch);

    RatingSearch.$inject = ['$resource'];

    function RatingSearch($resource) {
        var resourceUrl =  'api/_search/ratings/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

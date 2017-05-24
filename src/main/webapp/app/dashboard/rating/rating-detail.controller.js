(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('RatingDetailController', RatingDetailController);

    RatingDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Rating', 'User', 'Restaurant'];

    function RatingDetailController($scope, $rootScope, $stateParams, previousState, entity, Rating, User, Restaurant) {
        var vm = this;

        vm.rating = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gastronomeeApp:ratingUpdate', function(event, result) {
            vm.rating = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

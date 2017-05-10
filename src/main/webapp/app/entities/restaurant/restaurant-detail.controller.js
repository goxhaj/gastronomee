(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('RestaurantDetailController', RestaurantDetailController);

    RestaurantDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Restaurant', 'Location', 'User'];

    function RestaurantDetailController($scope, $rootScope, $stateParams, previousState, entity, Restaurant, Location, User) {
        var vm = this;

        vm.restaurant = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gastronomeeApp:restaurantUpdate', function(event, result) {
            vm.restaurant = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

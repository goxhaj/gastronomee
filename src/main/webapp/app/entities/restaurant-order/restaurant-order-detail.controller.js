(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('RestaurantOrderDetailController', RestaurantOrderDetailController);

    RestaurantOrderDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'RestaurantOrder', 'User', 'Restaurant'];

    function RestaurantOrderDetailController($scope, $rootScope, $stateParams, previousState, entity, RestaurantOrder, User, Restaurant) {
        var vm = this;

        vm.restaurantOrder = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gastronomeeApp:restaurantOrderUpdate', function(event, result) {
            vm.restaurantOrder = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('DishOrderDetailController', DishOrderDetailController);

    DishOrderDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'DishOrder', 'RestaurantOrder', 'Dish'];

    function DishOrderDetailController($scope, $rootScope, $stateParams, previousState, entity, DishOrder, RestaurantOrder, Dish) {
        var vm = this;

        vm.dishOrder = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gastronomeeApp:dishOrderUpdate', function(event, result) {
            vm.dishOrder = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

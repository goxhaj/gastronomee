(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('DishDetailController', DishDetailController);

    DishDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Dish', 'Menu', 'Ingredient', 'DishCategory'];

    function DishDetailController($scope, $rootScope, $stateParams, previousState, entity, Dish, Menu, Ingredient, DishCategory) {
        var vm = this;

        vm.dish = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gastronomeeApp:dishUpdate', function(event, result) {
            vm.dish = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

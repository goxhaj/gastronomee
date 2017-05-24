(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('IngredientDetailController', IngredientDetailController);

    IngredientDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Ingredient', 'Dish'];

    function IngredientDetailController($scope, $rootScope, $stateParams, previousState, entity, Ingredient, Dish) {
        var vm = this;

        vm.ingredient = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gastronomeeApp:ingredientUpdate', function(event, result) {
            vm.ingredient = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

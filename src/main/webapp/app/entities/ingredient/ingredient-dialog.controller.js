(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('IngredientDialogController', IngredientDialogController);

    IngredientDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Ingredient', 'Dish'];

    function IngredientDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Ingredient, Dish) {
        var vm = this;

        vm.ingredient = entity;
        vm.clear = clear;
        vm.save = save;
        vm.dishes = Dish.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.ingredient.id !== null) {
                Ingredient.update(vm.ingredient, onSaveSuccess, onSaveError);
            } else {
                Ingredient.save(vm.ingredient, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gastronomeeApp:ingredientUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

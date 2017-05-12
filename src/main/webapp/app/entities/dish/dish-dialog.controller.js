(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('DishDialogController', DishDialogController);

    DishDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Dish', 'Menu', 'Ingredient', 'DishCategory'];

    function DishDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Dish, Menu, Ingredient, DishCategory) {
        var vm = this;

        vm.dish = entity;
        vm.clear = clear;
        vm.save = save;
        vm.menus = Menu.myMenus();
        vm.ingredients = Ingredient.query();
        vm.dishcategories = DishCategory.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.dish.id !== null) {
                Dish.update(vm.dish, onSaveSuccess, onSaveError);
            } else {
                Dish.save(vm.dish, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gastronomeeApp:dishUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

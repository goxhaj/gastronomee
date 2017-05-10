(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('IngredientDeleteController',IngredientDeleteController);

    IngredientDeleteController.$inject = ['$uibModalInstance', 'entity', 'Ingredient'];

    function IngredientDeleteController($uibModalInstance, entity, Ingredient) {
        var vm = this;

        vm.ingredient = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Ingredient.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

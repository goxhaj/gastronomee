(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('DishCategoryDeleteController',DishCategoryDeleteController);

    DishCategoryDeleteController.$inject = ['$uibModalInstance', 'entity', 'DishCategory'];

    function DishCategoryDeleteController($uibModalInstance, entity, DishCategory) {
        var vm = this;

        vm.dishCategory = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            DishCategory.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

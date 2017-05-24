(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('DishDeleteController',DishDeleteController);

    DishDeleteController.$inject = ['$uibModalInstance', 'entity', 'Dish'];

    function DishDeleteController($uibModalInstance, entity, Dish) {
        var vm = this;

        vm.dish = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Dish.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

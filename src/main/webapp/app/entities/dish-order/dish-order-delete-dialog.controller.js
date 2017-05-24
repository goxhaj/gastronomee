(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('DishOrderDeleteController',DishOrderDeleteController);

    DishOrderDeleteController.$inject = ['$uibModalInstance', 'entity', 'DishOrder'];

    function DishOrderDeleteController($uibModalInstance, entity, DishOrder) {
        var vm = this;

        vm.dishOrder = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            DishOrder.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

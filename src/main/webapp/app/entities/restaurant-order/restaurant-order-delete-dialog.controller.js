(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('RestaurantOrderDeleteController',RestaurantOrderDeleteController);

    RestaurantOrderDeleteController.$inject = ['$uibModalInstance', 'entity', 'RestaurantOrder'];

    function RestaurantOrderDeleteController($uibModalInstance, entity, RestaurantOrder) {
        var vm = this;

        vm.restaurantOrder = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            RestaurantOrder.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

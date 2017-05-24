(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('RestaurantDeleteController',RestaurantDeleteController);

    RestaurantDeleteController.$inject = ['$uibModalInstance', 'entity', 'Restaurant'];

    function RestaurantDeleteController($uibModalInstance, entity, Restaurant) {
        var vm = this;

        vm.restaurant = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Restaurant.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('RatingDeleteController',RatingDeleteController);

    RatingDeleteController.$inject = ['$uibModalInstance', 'entity', 'Rating'];

    function RatingDeleteController($uibModalInstance, entity, Rating) {
        var vm = this;

        vm.rating = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Rating.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

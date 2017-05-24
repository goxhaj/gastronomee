(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('RestaurantOrderDialogController', RestaurantOrderDialogController);

    RestaurantOrderDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'RestaurantOrder', 'User', 'Restaurant'];

    function RestaurantOrderDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, RestaurantOrder, User, Restaurant) {
        var vm = this;

        vm.restaurantOrder = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();
        vm.restaurants = Restaurant.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.restaurantOrder.id !== null) {
                RestaurantOrder.update(vm.restaurantOrder, onSaveSuccess, onSaveError);
            } else {
                RestaurantOrder.save(vm.restaurantOrder, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gastronomeeApp:restaurantOrderUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.created = false;
        vm.datePickerOpenStatus.updated = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();

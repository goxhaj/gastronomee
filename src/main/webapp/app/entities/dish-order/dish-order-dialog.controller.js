(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('DishOrderDialogController', DishOrderDialogController);

    DishOrderDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'DishOrder', 'RestaurantOrder', 'Dish'];

    function DishOrderDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, DishOrder, RestaurantOrder, Dish) {
        var vm = this;

        vm.dishOrder = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.restaurantorders = RestaurantOrder.query();
        vm.dishes = Dish.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.dishOrder.id !== null) {
                DishOrder.update(vm.dishOrder, onSaveSuccess, onSaveError);
            } else {
                DishOrder.save(vm.dishOrder, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gastronomeeApp:dishOrderUpdate', result);
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

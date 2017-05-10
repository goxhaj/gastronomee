(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('DishCategoryDialogController', DishCategoryDialogController);

    DishCategoryDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'DishCategory'];

    function DishCategoryDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, DishCategory) {
        var vm = this;

        vm.dishCategory = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.dishCategory.id !== null) {
                DishCategory.update(vm.dishCategory, onSaveSuccess, onSaveError);
            } else {
                DishCategory.save(vm.dishCategory, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gastronomeeApp:dishCategoryUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

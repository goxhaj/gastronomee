(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('RestaurantDialogController', RestaurantDialogController);

    RestaurantDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Restaurant', 'Location', 'User'];

    function RestaurantDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Restaurant, Location, User) {
        var vm = this;

        vm.restaurant = entity;
        vm.clear = clear;
        vm.save = save;
        vm.locations = Location.query({filter: 'restaurant-is-null'});
        $q.all([vm.restaurant.$promise, vm.locations.$promise]).then(function() {
            if (!vm.restaurant.location || !vm.restaurant.location.id) {
                return $q.reject();
            }
            return Location.get({id : vm.restaurant.location.id}).$promise;
        }).then(function(location) {
            vm.locations.push(location);
        });
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.restaurant.id !== null) {
                Restaurant.update(vm.restaurant, onSaveSuccess, onSaveError);
            } else {
                Restaurant.save(vm.restaurant, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gastronomeeApp:restaurantUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('RestaurantDialogController', RestaurantDialogController);

    RestaurantDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Restaurant', 'Country'];

    function RestaurantDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Restaurant, Country) {
        var vm = this;

        vm.restaurant = entity;
        vm.clear = clear;
        vm.save = save;
        
        vm.countries = null;
    	vm.searchCountries = [];

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
            
            function onSaveSuccess (result) {
                $scope.$emit('gastronomeeApp:restaurantUpdate', result);
                $uibModalInstance.close(result);
                vm.isSaving = false;
            }

            function onSaveError () {
                vm.isSaving = false;
            }
            
        }

        
        vm.refreshCountries = function(name) {
        	if(name!=null && name!=''){
	        	Country.getCountries({name: name}, function(result) {
	        		vm.searchCountries=result;
	        		vm.countrys=result;
	            });
        	}
        };


    }
})();

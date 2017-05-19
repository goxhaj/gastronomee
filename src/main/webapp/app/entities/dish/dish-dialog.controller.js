(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('DishDialogController', DishDialogController);

    DishDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Dish', 'Menu', 'Ingredient', 'DishCategory'];

    function DishDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Dish, Menu, Ingredient, DishCategory) {
        var vm = this;

        vm.dish = entity;
        vm.clear = clear;
        vm.save = save;
        vm.menus = Menu.my();

        vm.dishcategories = DishCategory.active();
        
		vm.searchIngredients=[];
		vm.ingredients=[];

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.dish.id !== null) {
                Dish.update(vm.dish, onSaveSuccess, onSaveError);
            } else {
                Dish.save(vm.dish, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gastronomeeApp:dishUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }
        
        vm.loadIngredients = function(ingredient) {  
        	if(ingredient!=null && ingredient.name!=null){      		
        		vm.refreshIngredients(ingredient.name);       		
        	} else {
        		vm.ingredients = Ingredient.query({filter: 'ingredient-is-null'});               
        	}
            
        };
        
        vm.refreshIngredients = function(name) {
        	if(name!=null && name!=''){
	        	Ingredient.getIngredients({name: name}, function(result) {
	        		vm.searchIngredients=result;
	        		vm.ingredients=result;
	            });
        	}
        };


    }
})();

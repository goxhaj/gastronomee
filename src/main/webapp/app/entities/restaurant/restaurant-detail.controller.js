(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('RestaurantDetailController', RestaurantDetailController);

    RestaurantDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'AlertService', 'previousState', 'entity', 'Restaurant', 'Location', 'User'];

    function RestaurantDetailController($scope, $rootScope, $stateParams, AlertService, previousState, entity, Restaurant, Location, User ) {
        var vm = this;

        vm.restaurant = entity;
        vm.dishes = [];
        vm.previousState = previousState.name;
        
        loadAllDishes();
        
        function loadAllDishes () {
        	Restaurant.restaurantDishes({
        		id: vm.restaurant.id
            }, onSuccess, onError);
            
            function onSuccess(data, headers) {
                vm.dishes = data;
            }
            
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        
        var unsubscribe = $rootScope.$on('gastronomeeApp:restaurantUpdate', function(event, result) {
            vm.restaurant = result;
        });
        
        $scope.$on('$destroy', unsubscribe);
    }
})();

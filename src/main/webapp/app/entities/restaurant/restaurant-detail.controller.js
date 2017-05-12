(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('RestaurantDetailController', RestaurantDetailController);

    RestaurantDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Restaurant', 'Location', 'User', 'Menu'];

    function RestaurantDetailController($scope, $rootScope, $stateParams, previousState, entity, Restaurant, Location, User, Menu) {
        var vm = this;

        vm.restaurant = entity;
        vm.menus = [];
        vm.previousState = previousState.name;

        
        loadAllMenus();
        
        function loadAllMenus () {
        	Restaurant.menus({
        			id: vm.restaurant.id
            }, onSuccess, onError);
            
            function onSuccess(data, headers) {
                vm.menus = data;
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

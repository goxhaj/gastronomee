(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('MenuDetailController', MenuDetailController);

    MenuDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Menu', 'Restaurant'];

    function MenuDetailController($scope, $rootScope, $stateParams, previousState, entity, Menu, Restaurant) {
        var vm = this;

        vm.menu = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gastronomeeApp:menuUpdate', function(event, result) {
            vm.menu = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

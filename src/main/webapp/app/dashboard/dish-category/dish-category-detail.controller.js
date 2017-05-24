(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('DishCategoryDetailController', DishCategoryDetailController);

    DishCategoryDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'DishCategory'];

    function DishCategoryDetailController($scope, $rootScope, $stateParams, previousState, entity, DishCategory) {
        var vm = this;

        vm.dishCategory = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gastronomeeApp:dishCategoryUpdate', function(event, result) {
            vm.dishCategory = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

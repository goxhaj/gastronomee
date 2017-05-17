(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider     
        .state('restaurant-app-detail', {
            parent: 'app',
            url: '/restaurant/{id}',
            data: {
                authorities: [],
                pageTitle: 'gastronomeeApp.restaurant.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/restaurant/restaurant-detail.html',
                    controller: 'RestaurantAppDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('restaurant');
                    $translatePartialLoader.addPart('menu');
                    $translatePartialLoader.addPart('rating');
                    $translatePartialLoader.addPart('location');
                    $translatePartialLoader.addPart('dayOfWeek');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Restaurant', function($stateParams, Restaurant) {
                    return Restaurant.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || '^',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        });
    }

})();

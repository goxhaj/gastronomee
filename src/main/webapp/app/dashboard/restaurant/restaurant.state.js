(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('dashboard.restaurant', {
            parent: 'dashboard',
            url: '/dashboard-restaurant?page&sort&search',
            data: {
                authorities: [],
                pageTitle: 'gastronomeeApp.restaurant.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/dashboard/restaurant/restaurants.html',
                    controller: 'RestaurantController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('restaurant'); 
                    $translatePartialLoader.addPart('country');
                    $translatePartialLoader.addPart('location');
                    $translatePartialLoader.addPart('dayOfWeek');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('dashboard.restaurant-detail', {
            parent: 'dashboard',
            url: '/restaurant/detail/{id}',
            data: {
                authorities: ['ROLE_MANAGER','ROLE_ADMIN'],
                pageTitle: 'gastronomeeApp.restaurant.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/dashboard/restaurant/restaurant-detail.html',
                    controller: 'RestaurantDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('restaurant');
                    $translatePartialLoader.addPart('menu');
                    $translatePartialLoader.addPart('rating');
                    $translatePartialLoader.addPart('country');
                    $translatePartialLoader.addPart('location');
                    $translatePartialLoader.addPart('dayOfWeek');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Restaurant', function($stateParams, Restaurant) {
                    return Restaurant.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'dashboard',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('dashboard.restaurant-detail.edit', {
            parent: 'dashboard.restaurant-detail',
            url: '/restaurant/detail/edit',
            data: {
                authorities: ['ROLE_MANAGER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/restaurant/restaurant-dialog.html',
                    controller: 'RestaurantDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Restaurant', function(Restaurant) {
                            return Restaurant.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dashboard.restaurant.new', {
            parent: 'dashboard',
            url: '/restaurant/new',
            data: {
                authorities: ['ROLE_MANAGER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/restaurant/restaurant-dialog.html',
                    controller: 'RestaurantDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                open: null,
                                close: null,
                                tables: null,
                                chairs: null,
                                dayOfWeekClosed: null,
                                opened: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('dashboard', null, { reload: 'dashboard' });
                }, function() {
                    $state.go('dashboard');
                });
            }]
        })
        .state('dashboard.restaurant.edit', {
            parent: 'dashboard',
            url: '/restaurant/{id}/edit',
            data: {
                authorities: ['ROLE_MANAGER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/restaurant/restaurant-dialog.html',
                    controller: 'RestaurantDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Restaurant', function(Restaurant) {
                            return Restaurant.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dashboard', null, { reload: 'dashboard' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dashboard.restaurant.delete', {
            parent: 'dashboard',
            url: '/restaurant/{id}/delete',
            data: {
                authorities: ['ROLE_MANAGER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/restaurant/restaurant-delete-dialog.html',
                    controller: 'RestaurantDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Restaurant', function(Restaurant) {
                            return Restaurant.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dashboard', null, { reload: 'dashboard' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

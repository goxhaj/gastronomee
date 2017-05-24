(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('restaurant-order', {
            parent: 'entity',
            url: '/restaurant-order?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'gastronomeeApp.restaurantOrder.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/restaurant-order/restaurant-orders.html',
                    controller: 'RestaurantOrderController',
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
                    $translatePartialLoader.addPart('restaurantOrder');
                    $translatePartialLoader.addPart('restaurantOrderStatus');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('restaurant-order-detail', {
            parent: 'restaurant-order',
            url: '/restaurant-order/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'gastronomeeApp.restaurantOrder.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/restaurant-order/restaurant-order-detail.html',
                    controller: 'RestaurantOrderDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('restaurantOrder');
                    $translatePartialLoader.addPart('restaurantOrderStatus');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'RestaurantOrder', function($stateParams, RestaurantOrder) {
                    return RestaurantOrder.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'restaurant-order',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('restaurant-order-detail.edit', {
            parent: 'restaurant-order-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/restaurant-order/restaurant-order-dialog.html',
                    controller: 'RestaurantOrderDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['RestaurantOrder', function(RestaurantOrder) {
                            return RestaurantOrder.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('restaurant-order.new', {
            parent: 'restaurant-order',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/restaurant-order/restaurant-order-dialog.html',
                    controller: 'RestaurantOrderDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                rate: null,
                                persons: null,
                                comment: null,
                                created: null,
                                updated: null,
                                status: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('restaurant-order', null, { reload: 'restaurant-order' });
                }, function() {
                    $state.go('restaurant-order');
                });
            }]
        })
        .state('restaurant-order.edit', {
            parent: 'restaurant-order',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/restaurant-order/restaurant-order-dialog.html',
                    controller: 'RestaurantOrderDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['RestaurantOrder', function(RestaurantOrder) {
                            return RestaurantOrder.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('restaurant-order', null, { reload: 'restaurant-order' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('restaurant-order.delete', {
            parent: 'restaurant-order',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/restaurant-order/restaurant-order-delete-dialog.html',
                    controller: 'RestaurantOrderDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['RestaurantOrder', function(RestaurantOrder) {
                            return RestaurantOrder.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('restaurant-order', null, { reload: 'restaurant-order' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

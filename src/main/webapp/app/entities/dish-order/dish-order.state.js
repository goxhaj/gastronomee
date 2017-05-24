(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('dish-order', {
            parent: 'entity',
            url: '/dish-order?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'gastronomeeApp.dishOrder.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/dish-order/dish-orders.html',
                    controller: 'DishOrderController',
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
                    $translatePartialLoader.addPart('dishOrder');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('dish-order-detail', {
            parent: 'dish-order',
            url: '/dish-order/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'gastronomeeApp.dishOrder.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/dish-order/dish-order-detail.html',
                    controller: 'DishOrderDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('dishOrder');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'DishOrder', function($stateParams, DishOrder) {
                    return DishOrder.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'dish-order',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('dish-order-detail.edit', {
            parent: 'dish-order-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/dish-order/dish-order-dialog.html',
                    controller: 'DishOrderDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DishOrder', function(DishOrder) {
                            return DishOrder.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dish-order.new', {
            parent: 'dish-order',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/dish-order/dish-order-dialog.html',
                    controller: 'DishOrderDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                rate: null,
                                nr: null,
                                comment: null,
                                created: null,
                                updated: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('dish-order', null, { reload: 'dish-order' });
                }, function() {
                    $state.go('dish-order');
                });
            }]
        })
        .state('dish-order.edit', {
            parent: 'dish-order',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/dish-order/dish-order-dialog.html',
                    controller: 'DishOrderDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DishOrder', function(DishOrder) {
                            return DishOrder.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dish-order', null, { reload: 'dish-order' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dish-order.delete', {
            parent: 'dish-order',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/dish-order/dish-order-delete-dialog.html',
                    controller: 'DishOrderDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['DishOrder', function(DishOrder) {
                            return DishOrder.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dish-order', null, { reload: 'dish-order' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

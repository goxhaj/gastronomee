(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('dashboard.dish', {
            parent: 'dashboard',
            url: '/dish?page&sort&search',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                pageTitle: 'gastronomeeApp.dish.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/dashboard/dish/dishes.html',
                    controller: 'DishController',
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
                    $translatePartialLoader.addPart('dish');
                    $translatePartialLoader.addPart('ingredient');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('dashboard.dish-detail', {
            parent: 'dashboard',
            url: '/dish/{id}',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                pageTitle: 'gastronomeeApp.dish.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/dashboard/dish/dish-detail.html',
                    controller: 'DishDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('dish');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Dish', function($stateParams, Dish) {
                    return Dish.get({id : $stateParams.id}).$promise;
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
        .state('dashboard.dish-detail.edit', {
            parent: 'dashboard.dish-detail',
            url: '/dish/detail/edit',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/dish/dish-dialog.html',
                    controller: 'DishDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Dish', function(Dish) {
                            return Dish.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dashboard.dish.new', {
            parent: 'dashboard',
            url: '/dish/new',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/dish/dish-dialog.html',
                    controller: 'DishDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                recipe: null,
                                active: null,
                                priority: null,
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
        .state('dashboard.dish.edit', {
            parent: 'dashboard',
            url: '/dish/{id}/edit',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/dish/dish-dialog.html',
                    controller: 'DishDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Dish', function(Dish) {
                            return Dish.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dashboard', null, { reload: 'dashboard' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dashboard.dish.delete', {
            parent: 'dashboard',
            url: '/dish/{id}/delete',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/dish/dish-delete-dialog.html',
                    controller: 'DishDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Dish', function(Dish) {
                            return Dish.get({id : $stateParams.id}).$promise;
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

(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('dashboard.ingredient', {
            parent: 'dashboard',
            url: '/dashboard-ingredient?page&sort&search',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                pageTitle: 'gastronomeeApp.ingredient.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/dashboard/ingredient/ingredients.html',
                    controller: 'IngredientController',
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
                    $translatePartialLoader.addPart('ingredient');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('dashboard.ingredient-detail', {
            parent: 'dashboard',
            url: '/ingredient/{id}',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                pageTitle: 'gastronomeeApp.ingredient.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/dashboard/ingredient/ingredient-detail.html',
                    controller: 'IngredientDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('ingredient');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Ingredient', function($stateParams, Ingredient) {
                    return Ingredient.get({id : $stateParams.id}).$promise;
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
        .state('dashboard.ingredient-detail.edit', {
            parent: 'dashboard.ingredient-detail',
            url: '/ingredient/detail/edit',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/ingredient/ingredient-dialog.html',
                    controller: 'IngredientDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Ingredient', function(Ingredient) {
                            return Ingredient.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dashboard.ingredient.new', {
            parent: 'dashboard',
            url: '/ingredient/new',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/ingredient/ingredient-dialog.html',
                    controller: 'IngredientDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
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
        .state('dashboard.ingredient.edit', {
            parent: 'dashboard',
            url: '/ingredient/{id}/edit',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/ingredient/ingredient-dialog.html',
                    controller: 'IngredientDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Ingredient', function(Ingredient) {
                            return Ingredient.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dashboard', null, { reload: 'dashboard' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dashboard.ingredient.delete', {
            parent: 'dashboard',
            url: '/ingredient/{id}/delete',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/ingredient/ingredient-delete-dialog.html',
                    controller: 'IngredientDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Ingredient', function(Ingredient) {
                            return Ingredient.get({id : $stateParams.id}).$promise;
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

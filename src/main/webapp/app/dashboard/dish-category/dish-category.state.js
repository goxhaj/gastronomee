(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('dashboard.dish-category', {
            parent: 'dashboard',
            url: '/dashboard-dish-category?page&sort&search',
            data: {
                authorities: [],
                pageTitle: 'gastronomeeApp.dishCategory.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/dashboard/dish-category/dish-categories.html',
                    controller: 'DishCategoryController',
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
                    $translatePartialLoader.addPart('dishCategory');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('dashboard.dish-category-detail', {
            parent: 'dashboard',
            url: '/dish-category/{id}',
            data: {
                authorities: [],
                pageTitle: 'gastronomeeApp.dishCategory.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/dashboard/dish-category/dish-category-detail.html',
                    controller: 'DishCategoryDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('dishCategory');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'DishCategory', function($stateParams, DishCategory) {
                    return DishCategory.get({id : $stateParams.id}).$promise;
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
        .state('dashboard.dish-category-detail.edit', {
            parent: 'dashboard.dish-category-detail',
            url: '/dish-category/detail/edit',
            data: {
                authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/dish-category/dish-category-dialog.html',
                    controller: 'DishCategoryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DishCategory', function(DishCategory) {
                            return DishCategory.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dashboard.dish-category.new', {
            parent: 'dashboard',
            url: '/dish-category/new',
            data: {
                authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/dish-category/dish-category-dialog.html',
                    controller: 'DishCategoryDialogController',
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
        .state('dashboard.dish-category.edit', {
            parent: 'dashboard',
            url: '/dish-category/{id}/edit',
            data: {
                authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/dish-category/dish-category-dialog.html',
                    controller: 'DishCategoryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DishCategory', function(DishCategory) {
                            return DishCategory.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dashboard', null, { reload: 'dashboard' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dashboard.dish-category.delete', {
            parent: 'dashboard',
            url: '/dish-category/{id}/delete',
            data: {
                authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/dish-category/dish-category-delete-dialog.html',
                    controller: 'DishCategoryDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['DishCategory', function(DishCategory) {
                            return DishCategory.get({id : $stateParams.id}).$promise;
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

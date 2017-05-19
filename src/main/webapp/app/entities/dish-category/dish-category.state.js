(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('dish-category', {
            parent: 'entity',
            url: '/dish-category?page&sort&search',
            data: {
                authorities: [],
                pageTitle: 'gastronomeeApp.dishCategory.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/dish-category/dish-categories.html',
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
        .state('dish-category-detail', {
            parent: 'dish-category',
            url: '/dish-category/{id}',
            data: {
                authorities: [],
                pageTitle: 'gastronomeeApp.dishCategory.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/dish-category/dish-category-detail.html',
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
                        name: $state.current.name || 'dish-category',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('dish-category-detail.edit', {
            parent: 'dish-category-detail',
            url: '/detail/edit',
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
        .state('dish-category.new', {
            parent: 'dish-category',
            url: '/new',
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
                    $state.go('dish-category', null, { reload: 'dish-category' });
                }, function() {
                    $state.go('dish-category');
                });
            }]
        })
        .state('dish-category.edit', {
            parent: 'dish-category',
            url: '/{id}/edit',
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
                    $state.go('dish-category', null, { reload: 'dish-category' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dish-category.delete', {
            parent: 'dish-category',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/dish-category/dish-category-delete-dialog.html',
                    controller: 'DishCategoryDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['DishCategory', function(DishCategory) {
                            return DishCategory.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dish-category', null, { reload: 'dish-category' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

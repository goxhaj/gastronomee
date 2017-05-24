(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('dashboard.rating', {
            parent: 'dashboard',
            url: '/dashboard-rating',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                pageTitle: 'gastronomeeApp.rating.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/dashboard/rating/ratings.html',
                    controller: 'RatingController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('rating');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('dashboard.rating-detail', {
            parent: 'dashboard',
            url: '/rating/{id}',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                pageTitle: 'gastronomeeApp.rating.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/dashboard/rating/rating-detail.html',
                    controller: 'RatingDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('rating');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Rating', function($stateParams, Rating) {
                    return Rating.get({id : $stateParams.id}).$promise;
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
        .state('dashboard.rating-detail.edit', {
            parent: 'dashboard.rating-detail',
            url: '/rating/detail/edit',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/rating/rating-dialog.html',
                    controller: 'RatingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Rating', function(Rating) {
                            return Rating.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dashboard.rating.new', {
            parent: 'dashboard',
            url: '/rating/new',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/rating/rating-dialog.html',
                    controller: 'RatingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                rate: null,
                                comment: null,
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
        .state('dashboard.rating.edit', {
            parent: 'dashboard',
            url: '/rating/{id}/edit',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/rating/rating-dialog.html',
                    controller: 'RatingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Rating', function(Rating) {
                            return Rating.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dashboard', null, { reload: 'dashboard' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dashboard.rating.delete', {
            parent: 'dashboard',
            url: '/rating/{id}/delete',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/dashboard/rating/rating-delete-dialog.html',
                    controller: 'RatingDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Rating', function(Rating) {
                            return Rating.get({id : $stateParams.id}).$promise;
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

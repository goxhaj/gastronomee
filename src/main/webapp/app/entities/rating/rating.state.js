(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('rating', {
            parent: 'entity',
            url: '/rating',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                pageTitle: 'gastronomeeApp.rating.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/rating/ratings.html',
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
        .state('rating-detail', {
            parent: 'rating',
            url: '/rating/{id}',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                pageTitle: 'gastronomeeApp.rating.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/rating/rating-detail.html',
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
                        name: $state.current.name || 'rating',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('rating-detail.edit', {
            parent: 'rating-detail',
            url: '/detail/edit',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/rating/rating-dialog.html',
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
        .state('rating.new', {
            parent: 'rating',
            url: '/new',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/rating/rating-dialog.html',
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
                    $state.go('rating', null, { reload: 'rating' });
                }, function() {
                    $state.go('rating');
                });
            }]
        })
        .state('rating.edit', {
            parent: 'rating',
            url: '/{id}/edit',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/rating/rating-dialog.html',
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
                    $state.go('rating', null, { reload: 'rating' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('rating.delete', {
            parent: 'rating',
            url: '/{id}/delete',
            data: {
            	authorities: ['ROLE_MANAGER', 'ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/rating/rating-delete-dialog.html',
                    controller: 'RatingDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Rating', function(Rating) {
                            return Rating.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('rating', null, { reload: 'rating' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

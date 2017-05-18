(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'AlertService', 'Principal', 'LoginService', 'Restaurant', 'RestaurantSearch', '$state', 'ParseLinks', 'paginationConstants', 'pagingParams'];

    function HomeController ($scope, AlertService, Principal, LoginService, Restaurant, RestaurantSearch, $state, ParseLinks, paginationConstants, pagingParams) {
        var vm = this;
        
        vm.account = null;        
        vm.restaurants = [];
        
        vm.loadPage = loadPage;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = pagingParams.search;
        vm.currentSearch = pagingParams.search;

        loadAll();
        
        $scope.$on('authenticationSuccess', function() {
            getAccount();        
            $state.go("dashboard");
        });

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }        

        function loadAll () {
            if (pagingParams.search) {
                RestaurantSearch.query({
                    query: pagingParams.search,
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                }, onSuccess, onError);
            } else {
                Restaurant.query({
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                }, onSuccess, onError);
            }

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.restaurants = data;
                vm.page = pagingParams.page;
            }
            
            function onError(error) {
                AlertService.error(error.data.message);
            }
            
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                search: vm.currentSearch
            });
        }

        function search(searchQuery) {
            vm.links = null;
            vm.page = 1;
            vm.currentSearch = searchQuery;
            vm.transition();
        }

        
    }
})();

(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('DashboardController', DashboardController);

    DashboardController.$inject = ['$scope', 'Principal','AlertService', 'Restaurant', 'RestaurantSearch', 'Rating', '$state', 'ParseLinks', 'paginationConstants', 'pagingParams'];

    function DashboardController ($scope, Principal, AlertService, Restaurant, RestaurantSearch, Rating, $state, ParseLinks, paginationConstants, pagingParams) {
        var vm = this;
        
        vm.account = null;
        
        vm.restaurants = [];
        
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        
        vm.loadMyRestaurants = loadMyRestaurants;
        vm.loadMyRatings = loadMyRatings;
        
        vm.searchQuery = pagingParams.search;
        vm.currentSearch = pagingParams.search;
        
        Principal.identity().then(function(account) {
            vm.account = account;
        });

        loadMyRestaurants();
        loadMyRatings();
        

        function loadMyRestaurants () {
            if (pagingParams.search) {
                RestaurantSearch.my({
                    query: pagingParams.search,
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            } else {
                Restaurant.my({
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            }
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
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
        
        function loadMyRatings () {
        	Rating.my(onSuccess, onError);        
            function onSuccess(data, headers) {
                vm.ratings = data;
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
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }

        function search(searchQuery) {
            if (!searchQuery){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.currentSearch = null;
            vm.transition();
        }
        
    }
})();

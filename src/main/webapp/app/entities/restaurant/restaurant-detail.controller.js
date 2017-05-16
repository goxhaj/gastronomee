(function() {
    'use strict';

    angular
        .module('gastronomeeApp')
        .controller('RestaurantDetailController', RestaurantDetailController);

    RestaurantDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'AlertService', 'previousState', 'entity', 'Restaurant', 'Location', 'Rating'];

    function RestaurantDetailController($scope, $rootScope, $stateParams, AlertService, previousState, entity, Restaurant, Location, Rating) {

    	var vm = this;

        vm.restaurant = entity;
        vm.dishes = [];
        vm.ratings = [];
        vm.previousState = previousState.name;
        
        vm.rating={};
        vm.rating.restaurant=entity;
        vm.rating.rate=5;
        vm.max = 10;
        vm.isReadonly = false;
        vm.rate = rate;
        loadAllRatings();
        loadAllDishes();
        
        vm.lat=41.327953;
        if(vm.restaurant.location!=null){
        	vm.lat=vm.restaurant.location.lat;
        }
        vm.lng=19.819025;
        if(vm.restaurant.location!=null){
        	vm.lng=vm.restaurant.location.lng;
        }

        vm.hoveringOver = function(value) {
          vm.overStar = value;
          vm.percent = 100 * (value / vm.max);
        };

        vm.ratingStates = [
          {stateOn: 'glyphicon-ok-sign', stateOff: 'glyphicon-ok-circle'},
          {stateOn: 'glyphicon-star', stateOff: 'glyphicon-star-empty'},
          {stateOn: 'glyphicon-heart', stateOff: 'glyphicon-ban-circle'},
          {stateOn: 'glyphicon-heart'},
          {stateOff: 'glyphicon-off'}
        ];
        
    	angular.extend($scope, {
            center: {
                lat: vm.lat,
                lng: vm.lng,
                zoom: 6
            },
            markers: {
                taipei: {
                    lat: vm.lat,
                    lng: vm.lng,
                }
            },
            layers: {
                baselayers: {
                    mapbox_light: {
                        name: 'Mapbox Light',
                        type: 'mapbox',
                        user: 'elesdoar',
                        key: 'citojtj9e00022iqjmdzhrdwd',
                        apiKey: 'pk.eyJ1IjoiZWxlc2RvYXIiLCJhIjoiY2l0bmcwaDNpMDQzMTJvbDRpaTltN2dlbiJ9.KDnhRVh9St6vpQovMI7iLg'
                    },
                    osm: {
                        name: 'OpenStreetMap',
                        url: 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                        type: 'xyz'
                    }
                }
            }
        });
        

        function rate () {
            vm.isSaving = true;
            if (vm.rating.id !== null) {
                Rating.update(vm.rating, onSaveSuccess, onSaveError);
            } else {
                Rating.save(vm.rating, onSaveSuccess, onSaveError);
            }
            
            function onSaveSuccess (result) {
                $scope.$emit('gastronomeeApp:restaurantUpdate', result);
                vm.ratings.push(result);
                vm.isSaving = false;
            }

            function onSaveError () {
                vm.isSaving = false;
            }
        }
        

        function loadAllRatings () {
        	Restaurant.ratings({
        		id: vm.restaurant.id
            }, onSuccess, onError);
            
            function onSuccess(data, headers) {
                vm.ratings = data;
            }
            
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        
        
        
        function loadAllDishes () {
        	Restaurant.dishes({
        		id: vm.restaurant.id
            }, onSuccess, onError);
            
            function onSuccess(data, headers) {
                vm.dishes = data;
            }
            
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        
        var unsubscribe = $rootScope.$on('gastronomeeApp:restaurantUpdate', function(event, result) {
            vm.restaurant = result;
        });
        
        $scope.$on('$destroy', unsubscribe);


    }
})();

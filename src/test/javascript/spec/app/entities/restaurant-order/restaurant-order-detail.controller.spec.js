'use strict';

describe('Controller Tests', function() {

    describe('RestaurantOrder Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockRestaurantOrder, MockUser, MockRestaurant;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockRestaurantOrder = jasmine.createSpy('MockRestaurantOrder');
            MockUser = jasmine.createSpy('MockUser');
            MockRestaurant = jasmine.createSpy('MockRestaurant');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'RestaurantOrder': MockRestaurantOrder,
                'User': MockUser,
                'Restaurant': MockRestaurant
            };
            createController = function() {
                $injector.get('$controller')("RestaurantOrderDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'gastronomeeApp:restaurantOrderUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

'use strict';

describe('Controller Tests', function() {

    describe('DishOrder Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockDishOrder, MockRestaurantOrder, MockDish;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockDishOrder = jasmine.createSpy('MockDishOrder');
            MockRestaurantOrder = jasmine.createSpy('MockRestaurantOrder');
            MockDish = jasmine.createSpy('MockDish');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'DishOrder': MockDishOrder,
                'RestaurantOrder': MockRestaurantOrder,
                'Dish': MockDish
            };
            createController = function() {
                $injector.get('$controller')("DishOrderDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'gastronomeeApp:dishOrderUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

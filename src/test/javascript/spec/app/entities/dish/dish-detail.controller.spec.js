'use strict';

describe('Controller Tests', function() {

    describe('Dish Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockDish, MockMenu, MockIngredient, MockDishCategory;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockDish = jasmine.createSpy('MockDish');
            MockMenu = jasmine.createSpy('MockMenu');
            MockIngredient = jasmine.createSpy('MockIngredient');
            MockDishCategory = jasmine.createSpy('MockDishCategory');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Dish': MockDish,
                'Menu': MockMenu,
                'Ingredient': MockIngredient,
                'DishCategory': MockDishCategory
            };
            createController = function() {
                $injector.get('$controller')("DishDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'gastronomeeApp:dishUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

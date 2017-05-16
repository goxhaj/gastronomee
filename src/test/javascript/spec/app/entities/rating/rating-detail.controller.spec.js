'use strict';

describe('Controller Tests', function() {

    describe('Rating Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockRating, MockUser, MockRestaurant;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockRating = jasmine.createSpy('MockRating');
            MockUser = jasmine.createSpy('MockUser');
            MockRestaurant = jasmine.createSpy('MockRestaurant');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Rating': MockRating,
                'User': MockUser,
                'Restaurant': MockRestaurant
            };
            createController = function() {
                $injector.get('$controller')("RatingDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'gastronomeeApp:ratingUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

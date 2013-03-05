
function ListComputerCtrl($scope, ComputerService) {
    $scope.computers = ComputerService.query();
}

function NewComputerCtrl($scope) {
}

function EditComputerCtrl($scope) {
}

var app = angular.module('Computers', ['ComputersServices']);

app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/computers', {templateUrl:'partial/list.html', controller:ListComputerCtrl});
    $routeProvider.when('/computer/new', {templateUrl:'partial/editOrNew.html', controller:NewComputerCtrl});
    $routeProvider.when('/computer/:id', {templateUrl:'partial/editOrNew.html', controller:EditComputerCtrl});
    $routeProvider.otherwise({redirectTo: '/computers'});
}]);

app.directive('date', function (dateFilter) {
    return {
        require:'ngModel',
        link:function (scope, elm, attrs, ctrl) {
            var dateFormat = 'yyyy-MM-dd';
            var dateRegexp = /^[1-2][0-9][0-9][0-9]\-[01][0-9]\-[0-3][0-9]$/;

            ctrl.$parsers.unshift(function (viewValue) {
                if (dateRegexp.test(viewValue)) {
                    var parsedDateMilissec = Date.parse(viewValue);
                    if (parsedDateMilissec > 0) {
                        ctrl.$setValidity('date', true);
                        return parsedDateMilissec;
                    }
                }

                // in all other cases it is invalid, return undefined (no model update)
                ctrl.$setValidity('date', false);
                return undefined;
            });

            ctrl.$formatters.unshift(function (modelValue) {
                return dateFilter(modelValue, dateFormat);
            });
        }
    };
});

var services = angular.module('ComputersServices', ['ngResource']);

services.factory('ComputerService', function($resource) {
    return $resource('/computer/:id', {});
});
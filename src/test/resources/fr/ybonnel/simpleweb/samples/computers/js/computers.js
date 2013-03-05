
function ListComputerCtrl($scope, ComputerService) {
    $scope.computers = ComputerService.query();
}

function NewComputerCtrl($scope) {
}

function EditComputerCtrl($scope) {
}

angular.module('Computers', ['ComputersServices']).config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/computer', {templateUrl:'partial/list.html', controller:ListComputerCtrl});
    $routeProvider.when('/computer/new', {templateUrl:'partial/editOrNew.html', controller:NewComputerCtrl});
    $routeProvider.when('/computer/:id', {templateUrl:'partial/editOrNew.html', controller:EditComputerCtrl});
    $routeProvider.otherwise({redirectTo: '/computer'});
}]);

var services = angular.module('ComputersServices', ['ngResource']);

services.factory('ComputerService', function($resource) {
    return $resource('/computer/:id', {});
});
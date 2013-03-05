
function ListComputerCtrl($scope) {
}

function NewComputerCtrl($scope) {
}

function EditComputerCtrl($scope) {
}

angular.module('Computers', []).config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/computer', {templateUrl:'partial/list.html', controller:ListComputerCtrl});
    $routeProvider.when('/computer/new', {templateUrl:'partial/editOrNew.html', controller:NewComputerCtrl});
    $routeProvider.when('/computer/:id', {templateUrl:'partial/editOrNew.html', controller:EditComputerCtrl});
    $routeProvider.otherwise({redirectTo: '/computer'});
}]);

function FormCtrl() {

}

function ResultCtrl() {

}

angular.module('HelloWorld', []).config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/form', {templateUrl:'partial/form.html', controller:FormCtrl});
    $routeProvider.when('/result', {templateUrl:'partial/result.html', controller:ResultCtrl});
    $routeProvider.otherwise({redirectTo: '/form'});
}]);
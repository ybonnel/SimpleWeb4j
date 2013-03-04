
function FormCtrl($scope, $location) {
    $scope.submitForm = function(name, times, color) {
        if ($scope.form.$invalid) {
            $scope.form.name.$dirty = true;
            $scope.form.times.$dirty = true;
        } else {
            $location.path('/result/' + name + '/' + times + '/' + color);
        }
    }
}
FormCtrl.$inject = [ '$scope', '$location' ];

function ResultCtrl($scope, $routeParams) {
    $scope.name = $routeParams.name;
    $scope.times = Array(parseInt($routeParams.times));
    $scope.color = $routeParams.color;
    if ( $routeParams.color === '') {
        $scope.color = 'undefined';
    }
}
ResultCtrl.$inject = [ '$scope', '$routeParams' ];

angular.module('HelloWorld', []).config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/form', {templateUrl:'partial/form.html', controller:FormCtrl});
    $routeProvider.when('/result/:name/:times/:color', {templateUrl:'partial/result.html', controller:ResultCtrl});
    $routeProvider.otherwise({redirectTo: '/form'});
}]);
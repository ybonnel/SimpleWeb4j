
function SignupCtrl($scope) {

}

function ContactsCtrl($scope, $log) {
    $scope.profiles = [{phones:[{}]}];
    $scope.addNewProfile = function() {
        $scope.profiles.push({phones:[{}]});
    }

    $scope.removeProfile = function(index) {
        $scope.profiles.splice(index, 1);
    }

    $scope.addNewPhone = function(profile) {
        profile.phones.push({});
    }

    $scope.removePhone = function(profile, index) {
        profile.phones.splice(index, 1);
    }
}

var app = angular.module('Forms', []);

app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/forms', {templateUrl:'partial/forms.html'});
    $routeProvider.when('/signup', {templateUrl:'partial/signup.html', controller:SignupCtrl});
    $routeProvider.when('/contacts', {templateUrl:'partial/contacts.html', controller:ContactsCtrl});
    $routeProvider.otherwise({redirectTo: '/forms'});
}]);

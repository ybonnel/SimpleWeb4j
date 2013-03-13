

function SignupSummaryCtrl($scope, UserService) {
    $scope.user = UserService.getUser();
    $scope.nullValue = function(value) {
        if (typeof(value) === 'undefined') {
            return "Not specified";
        } else {
            return value;
        }
    }
}

function SignupCtrl($scope, $http, UserService, $location) {
    $http.get("/countries").success(function(data) {
        $scope.countries = data;
    });

    $scope.saveUser = function(user) {
        if ($scope.form.$invalid) {
            for (var name in $scope.form) {
                if (name.charAt(0) !== '$') {
                    $scope.form[name].$dirty=true;
                }
            }
        } else {
            UserService.setUser(user);
            $location.path('/signup/summary');
        }
    }
}

function ContactsCtrl($scope) {
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

var app = angular.module('Forms', ['FormsServices']);

app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/forms', {templateUrl:'partial/forms.html'});
    $routeProvider.when('/signup', {templateUrl:'partial/signup.html', controller:SignupCtrl});
    $routeProvider.when('/signup/summary', {templateUrl:'partial/summary.html', controller:SignupSummaryCtrl});
    $routeProvider.when('/contacts', {templateUrl:'partial/contacts.html', controller:ContactsCtrl});
    $routeProvider.otherwise({redirectTo: '/forms'});
}]);



app.directive('validateEquals', function() {
    return {
        require: 'ngModel',
        link: function(scope, elm, attrs, ctrl) {
            function validateEqual(myValue, otherValue) {
                ctrl.$setValidity('equal', myValue === otherValue);
                return myValue;
            }

            scope.$watch(attrs.validateEquals, function(otherModelValue) {
                validateEqual(ctrl.$viewValue, otherModelValue);
            });

            ctrl.$parsers.unshift(function(viewValue) {
                return validateEqual(viewValue, scope.$eval(attrs.validateEquals));
            });

            ctrl.$formatters.unshift(function(modelValue) {
                return validateEqual(modelValue, scope.$eval(attrs.validateEquals));
            });
        }
    };
});

var services = angular.module('FormsServices', []);

services.factory('UserService', function() {
    function UserService() {
        var currentUser;

        this.setUser = function(user) {
            currentUser = user;
        }

        this.getUser = function() {
            return currentUser;
        }
    }

    return new UserService();
});

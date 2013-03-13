

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

function allFormAttributeToDirty(form) {
    for (var name in form) {
        if (name.charAt(0) !== '$' || name === '$error') {
            if (typeof(form[name].$dirty) !== 'undefined') {
                form[name].$dirty=true;
            }
            allFormAttributeToDirty(form[name]);
        }
        if (form.$error) {

        }
    }
}

function SignupCtrl($scope, $http, UserService, $location) {
    $http.get("/countries").success(function(data) {
        $scope.countries = data;
    });

    $scope.saveUser = function(user) {
        if ($scope.form.$invalid) {
            allFormAttributeToDirty($scope.form);
        } else {
            UserService.setUser(user);
            $location.path('/signup/summary');
        }
    }
}

function ContactsCtrl($scope, ContactService, $location) {
    $scope.contact = {profiles:[{phones:[{}]}]};
    $scope.addNewProfile = function() {
        $scope.contact.profiles.push({phones:[{}]});
    }

    $scope.removeProfile = function(index) {
        $scope.contact.profiles.splice(index, 1);
    }

    $scope.addNewPhone = function(profile) {
        profile.phones.push({});
    }

    $scope.removePhone = function(profile, index) {
        profile.phones.splice(index, 1);
    }

    $scope.saveContact = function(contact) {
        if ($scope.form.$invalid) {
            allFormAttributeToDirty($scope.form);
        } else {
            ContactService.setContact(contact);
            $location.path('/contacts/summary');
        }
    }
}



function ContactsSummaryCtrl($scope, ContactService) {
    $scope.contact = ContactService.getContact();
    $scope.nullValue = function(value) {
        if (typeof(value) === 'undefined') {
            return "Not specified";
        } else {
            return value;
        }
    }
}

var app = angular.module('Forms', ['FormsServices']);

app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/forms', {templateUrl:'partial/forms.html'});
    $routeProvider.when('/signup', {templateUrl:'partial/signup.html', controller:SignupCtrl});
    $routeProvider.when('/signup/summary', {templateUrl:'partial/signupSummary.html', controller:SignupSummaryCtrl});
    $routeProvider.when('/contacts', {templateUrl:'partial/contacts.html', controller:ContactsCtrl});
    $routeProvider.when('/contacts/summary', {templateUrl:'partial/contactsSummary.html', controller:ContactsSummaryCtrl});
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

services.factory('ContactService', function() {
    function ContactService() {
        var currentContact;

        this.setContact = function(contact) {
            for (var indexProfile = 0, lenProfiles = contact.profiles.length; indexProfile < lenProfiles; indexProfile++) {
                var phones = new Array();
                for (var indexPhone = 0, lenPhones = contact.profiles[indexProfile].phones.length; indexPhone < lenPhones; indexPhone++) {
                    if (typeof(contact.profiles[indexProfile].phones[indexPhone].number) !== 'undefined'
                            && contact.profiles[indexProfile].phones[indexPhone].number.length > 0) {
                        phones.push(contact.profiles[indexProfile].phones[indexPhone]);
                    }
                }
                contact.profiles[indexProfile].phones = phones;
            }
            currentContact = contact;
        }

        this.getContact = function() {
            return currentContact;
        }
    }

    return new ContactService();
});

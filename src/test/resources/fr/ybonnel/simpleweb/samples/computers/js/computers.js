
function ListComputerCtrl($scope, ComputerService, MessageService, $log) {

    $scope.messages = MessageService.consumMessages();
    $log.info($scope.messages);
    $scope.computers = ComputerService.query();
}

function NewComputerCtrl($scope, CompanyService, ComputerService, $location) {
    $scope.canDelete = false;
    $scope.companies = CompanyService.query();
    $scope.loading = false;
    $scope.messageLoading = 'Creating your computer...';
    $scope.submitMessage = "Create this computer";
    $scope.saveComputer = function(computer) {
        if ($scope.form.$invalid) {
            $scope.form.name.$dirty = true;
            $scope.form.introduced.$dirty = true;
            $scope.form.discontinued.$dirty = true;
        } else {
            $scope.error = false;
            $scope.loading = true;
            ComputerService.save(computer, function(data) {
                $location.path('/computers');
            }, function(err) {
                $scope.loading = false;
                $scope.error = true;
                $scope.errorMessage = err.data;
            });
        }
    }
}

function EditComputerCtrl($scope, $routeParams, CompanyService, ComputerService, $location, MessageService) {
    $scope.canDelete = true;
    $scope.loading = false;
    $scope.submitMessage = "Save this computer";
    $scope.computer = ComputerService.get({id:$routeParams.id});
    $scope.companies = CompanyService.query();
    $scope.saveComputer = function(computer) {
        if ($scope.form.$invalid) {
            $scope.form.name.$dirty = true;
            $scope.form.introduced.$dirty = true;
            $scope.form.discontinued.$dirty = true;
        } else {
            $scope.error = false;
            $scope.messageLoading = 'Saving your computer...';
            $scope.loading = true;
            ComputerService.update(computer, function(data) {
                $location.path('/computers');
            }, function(err) {
                $scope.loading = false;
                $scope.error = true;
                $scope.errorMessage = err.data;
            });
        }
    };

    $scope.deleteComputer = function(computer) {
        $scope.messageLoading = 'Deleting your computer...';
        $scope.error = false;
        $scope.loading = true;
        ComputerService.delete({id:computer.id}, function(data) {
            MessageService.addMessage("<b>Done!</b> Computer has been deleted");
            $location.path('/computers');
        }, function(err) {
            $scope.loading = false;
            $scope.error = true;
            $scope.errorMessage = err.data;
        });
    };
}

var app = angular.module('Computers', ['ComputersServices', 'ngSanitize']);

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
                        return viewValue + 'T00:00:00.000Z';
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
    return $resource('/computer/:id',
        {id: "@id"},
        {update: {method:'PUT'}}
    );
});

services.factory('CompanyService', function($resource) {
    return $resource('/company/:id');
});

services.factory('MessageService', function() {
    function MessageService() {
        var messages = new Array();

        this.addMessage = function(message) {
            messages.push(message);
        }

        this.consumMessages = function() {
            var messagesToReturn = new Array();
            while (messages.length > 0) {
                messagesToReturn.push(messages.shift());
            }
            return messagesToReturn;
        }
    }

    return new MessageService();
});
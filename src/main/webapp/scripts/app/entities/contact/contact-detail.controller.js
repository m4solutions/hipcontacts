'use strict';

angular.module('hipcontactsApp')
    .controller('ContactDetailController', function ($scope, $stateParams, Contact, User) {
        $scope.contact = {};
        $scope.load = function (id) {
            Contact.get({id: id}, function(result) {
              $scope.contact = result;
            });
        };
        $scope.load($stateParams.id);
    });

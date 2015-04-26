'use strict';

angular.module('hipcontactsApp')
    .controller('ContactController', function ($scope, Contact, User, ParseLinks) {
        $scope.contacts = [];
        $scope.users = User.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Contact.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.contacts = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            Contact.update($scope.contact,
                function () {
                    $scope.loadAll();
                    $('#saveContactModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Contact.get({id: id}, function(result) {
                $scope.contact = result;
                $('#saveContactModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Contact.get({id: id}, function(result) {
                $scope.contact = result;
                $('#deleteContactConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Contact.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteContactConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.contact = {firstName: null, lastName: null, mobile: null, email: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });

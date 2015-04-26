'use strict';

angular.module('hipcontactsApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });

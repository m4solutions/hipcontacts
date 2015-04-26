'use strict';

angular.module('hipcontactsApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });



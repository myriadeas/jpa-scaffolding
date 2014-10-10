'use strict';

/* App Module */

angular.module('phonecat', ['phonecatFilters', 'phonecatServices','customerServices']).
  config(['$routeProvider', function($routeProvider) {
  $routeProvider.
      when('/home', {templateUrl: 'partials/home.html',   controller: EntityListCtrl}).
      when('/customer/', {templateUrl: 'partials/customer/list.html',   controller: CustomerListCtrl}).
      when('/customer/create', {templateUrl: 'partials/customer/create.html',   controller: CustomerCreateCtrl}).
      when('/customer/:id', {templateUrl: 'partials/customer/edit.html',   controller: CustomerEditCtrl}).
      otherwise({redirectTo: '/home'});
}]);


dependencies = [
    'ngRoute',
    'ui.bootstrap',
    'myApp.filters',
    'myApp.services',
    'myApp.controllers',
    'myApp.directives',
    'myApp.common',
    'myApp.routeConfig'
]

app = angular.module('myApp', dependencies)

angular.module('myApp.routeConfig', ['ngRoute'])
    .config(['$routeProvider', ($routeProvider) ->
        $routeProvider
            .when('/', {
                templateUrl: '/assets/partials/storageView.html'
            })
            .when('/users/create', {
                templateUrl: '/assets/partials/userCreate.html'
            })
            .when('/users/edit/:firstName/:lastName', {
                templateUrl: '/assets/partials/userUpdate.html'
            })
            .when('/product', {
                templateUrl: '/assets/partials/productView.html'
            })
            .when('/product/create', {
                templateUrl: '/assets/partials/productCreate.html'
            })
            .when('/storage', {
                templateUrl: '/assets/partials/storageView.html'
            })
            .when('/storage/create', {
                templateUrl: '/assets/partials/storageCreate.html'
            })
            .when('/storage/details/:opId', {
                templateUrl: '/assets/partials/storageDetailsView.html'
            })
            .when('/storage/picking/:opId', {
                templateUrl: '/assets/partials/storagePicking.html'
            })
            .otherwise({redirectTo: '/'})])
    .config(['$locationProvider', ($locationProvider) ->
        $locationProvider.html5Mode({
            enabled: true,
            requireBase: false
        })])

@commonModule = angular.module('myApp.common', [])
@controllersModule = angular.module('myApp.controllers', [])
@servicesModule = angular.module('myApp.services', [])
@modelsModule = angular.module('myApp.models', [])
@directivesModule = angular.module('myApp.directives', [])
@filtersModule = angular.module('myApp.filters', [])
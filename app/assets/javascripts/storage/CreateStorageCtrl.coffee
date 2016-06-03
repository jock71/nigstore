
class CreateStorageCtrl

    constructor: (@$log, @StorageService) ->
        @$log.debug "constructing CreateStorageController"
        @products = []
        @getAllProducts()
        @newEntry = {}

    getAllProducts: () ->
        @$log.debug "getAllProducts()"

        @StorageService.listProducts()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Products"
                @products = data
            ,
            (error) =>
                @$log.error "Unable to get Products: #{error}"
            )

    selectProduct: (product) ->
        @$log.debug "selectProduct(#{product})"
        @newEntry.product = product

    create: () ->
        @$log.debug "create() with #{@newEntry}"
        @StorageService.createStorageEntry(@newEntry)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data} create newEntry"
                @newEntry = data
                @$location.path("/")
            ,
            (error) =>
                @$log.error "Unable to create newEntry: #{error}"
            )


controllersModule.controller('CreateStorageCtrl', ['$log', 'StorageService', CreateStorageCtrl])
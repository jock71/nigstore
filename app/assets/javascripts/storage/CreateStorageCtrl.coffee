
class CreateStorageCtrl

    constructor: (@$log, @$location, @StorageService) ->
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
        @newEntry.pickings = []
        @newEntry.remainingQuantity = @newEntry.initialQuantity
        @newEntry._id =   "000000000000000000000000"  # ObjectID.generate()
        # @newEntry._id = "57517a30a65242ae0e5c4db2"  # ObjectID.generate()
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


controllersModule.controller('CreateStorageCtrl', ['$log', '$location', 'StorageService', CreateStorageCtrl])
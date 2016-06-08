
class StockCtrl

    constructor: (@$log, @$scope, @StockService) ->
        @$log.debug "constructing StockController"
        @stockEntries = []
        @registerEvents()

    loadProdStock: (prodId) ->
        @$log.debug "loadProdStock()"

        @StockService.getStockForProd(prodId)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Entries"
                @stockEntries = data
            ,
            (error) =>
                @$log.error "Unable to get Storage Entries: #{error}"
            )

    registerEvents: () ->
        @$log.debug "registerEvents()"
        @$scope.$on('PROD_SELECTED', (event, args) =>
            @$log.debug "product selected id = #{args.message}"
            @loadProdStock(args.message)
         );

controllersModule.controller('StockCtrl', ['$log', '$scope', 'StockService', StockCtrl])
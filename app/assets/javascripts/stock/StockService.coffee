
class StockService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q) ->
        @$log.debug "constructing StockService"

    getStockForProd: (_id) ->
        @$log.debug "getStockForProd(#{_id})"
        deferred = @$q.defer()

        @$http.get("/stock/prodId/#{_id}")
        .success((data, status, headers) =>
                 @$log.info("Successfully retrieved stock - status #{status}")
                 deferred.resolve(data)
            )
        .error((data, status, headers) =>
                 @$log.error("Failed to retrieve stock entry - status #{status}")
                 deferred.reject(data)
            )
        deferred.promise


servicesModule.service('StockService', ['$log', '$http', '$q', StockService])
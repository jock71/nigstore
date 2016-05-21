
class StorageService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q) ->
        @$log.debug "constructing StorageService"

    listStorageEntries: () ->
        @$log.debug "listStorageEntries()"
        deferred = @$q.defer()

        @$http.get("/storageEntries")
        .success((data, status, headers) =>
                @$log.info("Successfully listed storage entries - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to list storage entries - status #{status}")
                deferred.reject(data)
            )
        deferred.promise


servicesModule.service('StorageService', ['$log', '$http', '$q', StorageService])
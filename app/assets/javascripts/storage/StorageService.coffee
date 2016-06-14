
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

    getStorageEntry: (_id) ->
        @$log.debug "getStorageEntry(#{_id})"
        deferred = @$q.defer()

        @$http.get("/storageEntry/#{_id}")
        .success((data, status, headers) =>
                 @$log.info("Successfully retrieved storage entry - status #{status}")
                 deferred.resolve(data)
            )
        .error((data, status, headers) =>
                 @$log.error("Failed to retrieve storage entry - status #{status}")
                 deferred.reject(data)
            )
        deferred.promise


    listProducts: () ->
        @$log.debug "listProducts()"
        deferred = @$q.defer()

        @$http.get("/products")
        .success((data, status, headers) =>
                @$log.info("Successfully listed product entries - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to list product entries - status #{status}")
                deferred.reject(data)
            )
        deferred.promise


    createStorageEntry: (storageEntry) ->
        @$log.debug "createStorageEntry #{angular.toJson(storageEntry, true)}"
        deferred = @$q.defer()

        @$http.post('/storageEntry', storageEntry)
        .success((data, status, headers) =>
                 @$log.info("Successfully created storageEntry- status #{status}")
                 deferred.resolve(data)
             )
        .error((data, status, headers) =>
                 @$log.error("Failed to create storageEntry - status #{status}")
                 deferred.reject(data)
              )
        deferred.promise


    addPicking: (_id, picking) ->
        @$log.debug "addPicking(#{_id}, #{picking})"
        deferred = @$q.defer()

        @$http.put("/storageEntry/picking/#{_id}", picking)
        .success((data, status, headers) =>
                 @$log.info("Successfully appended picking entry - status #{status}")
                 deferred.resolve(data)
            )
        .error((data, status, headers) =>
                 @$log.error("Failed to append picking - status #{status}")
                 deferred.reject(data)
            )
        deferred.promise

servicesModule.service('StorageService', ['$log', '$http', '$q', StorageService])

class StorageCtrl

    constructor: (@$log, @StorageService) ->
        @$log.debug "constructing StorageController"
        @entries = []
        @getAllEntries()

    getAllEntries: () ->
        @$log.debug "getAllEntries()"

        @StorageService.listStorageEntries()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Entries"
                @entries = data
            ,
            (error) =>
                @$log.error "Unable to get Storage Entries: #{error}"
            )


controllersModule.controller('StorageCtrl', ['$log', 'StorageService', StorageCtrl])
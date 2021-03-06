
class StorageDetailsCtrl

    constructor: (@$log, @$routeParams, @StorageService) ->
        @$log.debug "constructing StorageController"
        @entry = {}
        @findEntry()

    findEntry: () ->
      # route params must be same name as provided in routing url in app.coffee
      opId = @$routeParams.opId
      @$log.debug "findEntry route params: #{opId}"

      @StorageService.getStorageEntry(opId)
      .then(
        (data) =>
          @$log.debug "Promise returned #{data.length} storage entries"

          # find a entry with opId
          # as filter returns an array, get the first object in it, and return it
          @entry = (data.filter (entry) -> entry._id is opId)[0]
      ,
        (error) =>
          @$log.error "Unable to get Entry: #{error}"
      )

controllersModule.controller('StorageDetailsCtrl', ['$log', '$routeParams', 'StorageService', StorageDetailsCtrl])
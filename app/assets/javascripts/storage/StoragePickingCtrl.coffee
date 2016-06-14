
class StoragePickingCtrl

    constructor: (@$log, @$routeParams, @StorageService) ->
        @$log.debug "constructing StoragePickingCtrl"
        @entry = {}
        @findEntry()
        @picking = {}

    findEntry: () ->
      # route params must be same name as provided in routing url in app.coffee
      opId = @$routeParams._id
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

    appendPicking: () ->
      _id = @$routeParams._id
      @picking.pickId = "1232321312312"
      @$log.debug "appending #{@picking} to storageID #{_id}"

      @StorageService.addPicking(_id, @picking)
      .then(
        (data) =>
          @$log.debug "promise returned"
        ,
        (error) =>
          @$log.error "Unable to append picking: #{error}"
      )

controllersModule.controller('StoragePickingCtrl', ['$log', '$routeParams', 'StorageService', StoragePickingCtrl])
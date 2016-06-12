
class NavBarCtrl

    constructor: (@$log, @$location) ->
        @$log.debug "constructing NavBarCtrl"

    isActive: (url) ->
        @$log.debug "isActive(#{url}) - location=#{@$location.path()}"
        url == @$location.path()


controllersModule.controller('NavBarCtrl', ['$log', '$location', NavBarCtrl])
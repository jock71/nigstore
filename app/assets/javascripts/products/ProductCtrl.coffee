


class ProductCtrl

    constructor: (@$log, @ProductService) ->
        @$log.debug "constructing ProductController"
        @products = []
        @sortKey = 'name'
        @sortReverse = false
        @getAllProducts()

    getAllProducts: () ->
        @$log.debug "getAllProducts()"

        @ProductService.listProducts()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Products"
                @products = data
            ,
            (error) =>
                @$log.error "Unable to get Products: #{error}"
            )

    sort: (keyname) ->
        @$log.debug "sort(#{keyname})"
        @sortKey = keyname
        @sortReverse = !@sortReverse

controllersModule.controller('ProductCtrl', ['$log', 'ProductService', ProductCtrl])

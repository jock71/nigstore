


class ProductCtrl

    constructor: (@$log, @$scope, @ProductService) ->
        @$log.debug "constructing ProductController"
        @products = []
        @sortKey = 'name'
        @sortReverse = false
        @selectedId = "-1"
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

    setSelectedProduct: (idSelected) ->
        @$log.debug "procutSelected(#{idSelected})"
        @selectedId = idSelected
        @$scope.$emit('PROD_SELECTED', { message: idSelected });

    getSelectedProduct: () ->
        @selectedId

    sort: (keyname) ->
        @$log.debug "sort(#{keyname})"
        @sortKey = keyname
        @sortReverse = !@sortReverse

controllersModule.controller('ProductCtrl', ['$log', '$scope', 'ProductService', ProductCtrl])

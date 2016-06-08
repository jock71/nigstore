
class CreateProductCtrl

    constructor: (@$log, @$location,  @ProductService) ->
        @$log.debug "constructing CreateProductController"
        @product = {}
        @categories = []
        @loadCategories()

    createProduct: () ->
        @$log.debug "createProduct()"
        @product._id =  "000000000000000000000000"
        @ProductService.createProduct(@product)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data} Product"
                @product = data
                @$location.path("/")
            ,
            (error) =>
                @$log.error "Unable to create Product: #{error}"
            )

    loadCategories: () ->
        @$log.debug "loadCategories()"
        @ProductService.loadCategories()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} categories"
                @categories = data
            ,
            (error) =>
                #$log.error "Unable to load product categories: #{error}"
            )


controllersModule.controller('CreateProductCtrl', ['$log', '$location', 'ProductService', CreateProductCtrl])
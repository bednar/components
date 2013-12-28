class Localization
    constructor: (@values) ->

    byKey: (key) ->
        @values[key]
class Localization

    constructor: () ->

    instance = null

    # Get Localization value by Key. Always not null
    @get: (key) ->

        # Init singleton
        if not @instance?
            instance = new @

            # Init localization values
            if not window.localization?
                window.localization = {}

        instance._get(key)


    # private
    _get: (key) ->
        if window.localization[key]?
            window.localization[key]
        else
            ''

@Components.Localization = Localization
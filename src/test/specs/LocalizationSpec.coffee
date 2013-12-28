describe 'Localization testing', ->

    it 'Value for undefined window.localization', ->

        expect(Components.Localization.get('any.key')).toBe('')

    it 'Value for undefined key', ->

        window.localization = {'app.key' : 'app key value'}
        
        expect(Components.Localization.get('any.key')).toBe('')

    it 'Value for defined key', ->

        window.localization = {'app.key' : 'app key value'}

        expect(Components.Localization.get('app.key')).toBe('app key value')
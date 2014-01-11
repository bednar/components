describe 'Components testing', ->

    it 'window has Components', ->

        expect(window.Components).toBeDefined()
        expect(window.Components).not.toBe(null)

    it 'Components has templates', ->

        expect(window.Components.templates).toBeDefined()
        expect(window.Components.templates).not.toBe(null)
package com.github.bednar.components.inject.service;

import com.github.bednar.components.AbstractComponentTest;
import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.JavaScriptException;

/**
 * @author Jakub Bednář (29/12/2013 18:17)
 */
public class JadeCompilerTest extends AbstractComponentTest
{
    @Test
    public void serviceNotNull()
    {
        JadeCompiler compiler = injector.getInstance(JadeCompiler.class);

        Assert.assertNotNull(compiler);
    }

    @Test
    public void compileByPath()
    {
        JadeCompiler compiler = injector.getInstance(JadeCompiler.class);

        String compiled = compiler.compile("/jade/basic.jade");

        Assert.assertEquals(
                "function template(locals) {var buf = [];var jade_mixins = {};" +
                "buf.push(\"<h1>Jade - node template engine</h1><p class=\"class\">Get on it!<span class=\"hello\">Bye Bye</span></p>\");;" +
                "return buf.join(\"\");}", compiled);
    }

    @Test
    public void compileByURL()
    {
        JadeCompiler compiler = injector.getInstance(JadeCompiler.class);

        String compiled = compiler.compile(this.getClass().getResource("/jade/basic.jade"));

        Assert.assertEquals(
                "function template(locals) {var buf = [];var jade_mixins = {};" +
                "buf.push(\"<h1>Jade - node template engine</h1><p class=\"class\">Get on it!<span class=\"hello\">Bye Bye</span></p>\");;" +
                "return buf.join(\"\");}", compiled);
    }

    @Test(expected = JavaScriptException.class)
    public void compileError()
    {
        JadeCompiler compiler = injector.getInstance(JadeCompiler.class);

        compiler.compile("/jade/error.jade");
    }
}


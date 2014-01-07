package com.github.bednar.components.inject.service;

import com.github.bednar.components.AbstractComponentTest;
import com.github.bednar.components.inject.service.resource.ResourceResponse;
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

    @Test
    public void acceptedType()
    {
        JadeCompiler processor = injector.getInstance(JadeCompiler.class);

        Assert.assertTrue(processor.isAcceptedType("/jade/basic.jade"));
    }

    @Test
    public void notAcceptedType()
    {
        JadeCompiler processor = injector.getInstance(JadeCompiler.class);

        Assert.assertFalse(processor.isAcceptedType("/less/basic.less"));
    }

    @Test
    public void processExistResource()
    {
        JadeCompiler processor = injector.getInstance(JadeCompiler.class);

        ResourceResponse response = processor.process("/jade/remote.jade", JadeCompilerCfg.build());

        Assert.assertNotNull(response);
        Assert.assertEquals((Object) 162, response.getContentLength());
        Assert.assertEquals("text/html;charset=UTF-8", response.getContentType());
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        Assert.assertEquals("p .for-remote-test{margin:10px}", new String(response.getContent()));
    }

    @Test
    public void processNotExistResource()
    {
        JadeCompiler processor = injector.getInstance(JadeCompiler.class);

        ResourceResponse response = processor.process("/jade/notexist.jade", JadeCompilerCfg.build());

        Assert.assertNotNull(response);
        Assert.assertEquals((Object) 50, response.getContentLength());
        Assert.assertEquals("text/html;charset=UTF-8", response.getContentType());
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        Assert.assertEquals("<!-- Resource: '/jade/notexist.jade' not exist -->", new String(response.getContent()));
    }
}


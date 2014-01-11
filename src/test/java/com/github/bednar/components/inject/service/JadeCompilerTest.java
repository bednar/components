package com.github.bednar.components.inject.service;

import com.github.bednar.base.utils.resource.FluentResource;
import com.github.bednar.components.AbstractComponentTest;
import com.github.bednar.components.inject.service.resource.ResourceResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
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
    public void compileAsHTML()
    {
        JadeCompiler compiler = injector.getInstance(JadeCompiler.class);

        JadeCompilerCfg configuration = JadeCompilerCfg
                .build()
                .setRenderAsHTML()
                .setRenderPretty();

        String compiled = compiler.compile("/jade/basic.jade", configuration);

        Assert.assertEquals(
                "\n<h1>Jade - node template engine</h1>\n" +
                "<p class=\"class\">Get on it!<span class=\"hello\">Bye Bye</span></p>", compiled);
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
    public void compileComplexTemplateAsHTML()
    {
        JadeCompiler compiler = injector.getInstance(JadeCompiler.class);

        JadeCompilerCfg configuration = JadeCompilerCfg
                .build()
                .setRenderAsHTML()
                .setRenderPretty();

        String compiled = compiler.compile("/jade/complex.jade", configuration);

        Assert.assertEquals(
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <title></title>\n" +
                "    <script type=\"text/javascript\">\n" +
                "      if (foo) {\n" +
                "         bar(1 + 5)\n" +
                "      }\n" +
                "    </script>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <h1>Jade - node template engine</h1>\n" +
                "    <div id=\"container\" class=\"col\">\n" +
                "      <p>Get on it!</p>\n" +
                "      <p>\n" +
                "        Jade is a terse and simple\n" +
                "        templating language with a\n" +
                "        strong focus on performance\n" +
                "        and powerful features.\n" +
                "      </p>\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>", compiled);
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
        Assert.assertEquals("application/javascript;charset=UTF-8", response.getContentType());
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        Assert.assertEquals(
                        "function template(locals) {var buf = [];var jade_mixins = {};" +
                        "buf.push(\"<h1 class=\"super-toolkit\">Hi Jakub... Super Toolkit!<p>NP</p></h1>\");" +
                        ";return buf.join(\"\");}",
                new String(response.getContent()));
    }

    @Test
    public void processNotExistResource()
    {
        JadeCompiler processor = injector.getInstance(JadeCompiler.class);

        ResourceResponse response = processor.process("/jade/notexist.jade", JadeCompilerCfg.build());

        Assert.assertNotNull(response);
        Assert.assertEquals((Object) 44, response.getContentLength());
        Assert.assertEquals("application/javascript;charset=UTF-8", response.getContentType());
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        Assert.assertEquals("// Resource: '/jade/notexist.jade' not exist", new String(response.getContent()));
    }

    @Test
    public void assignToVariable()
    {
        JadeCompiler processor = injector.getInstance(JadeCompiler.class);

        //check cached templates is assigned
        processor.process("/jade/assignTo.jade", JadeCompilerCfg.build());

        JadeCompilerCfg configuration = JadeCompilerCfg
                .build()
                .setAssignTo("window.templates");

        ResourceResponse response = processor.process("/jade/assignTo.jade", configuration);

        Assert.assertNotNull(response);
        Assert.assertEquals((Object) 163, response.getContentLength());
        Assert.assertEquals("application/javascript;charset=UTF-8", response.getContentType());
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        Assert.assertEquals("window.templates.assignTo = function template(locals) {var buf = [];" +
                "var jade_mixins = {};buf.push(\"<h1>Template for assignTo testing</h1>\");;" +
                "return buf.join(\"\");};", new String(response.getContent()));
    }

    @Test
    public void compileMultiple()
    {
        JadeCompiler processor = injector.getInstance(JadeCompiler.class);

        JadeCompilerCfg configuration = JadeCompilerCfg
                .build()
                .setAssignTo("window.templates")
                .setMultiple("/multiple/.*\\.jade");

        ResourceResponse response = processor.process("/jade/assignTo.jade", configuration);

        Assert.assertNotNull(response);
        Assert.assertEquals((Object) 332, response.getContentLength());
        Assert.assertEquals("application/javascript;charset=UTF-8", response.getContentType());
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        Assert.assertEquals(
                "window.templates.template2 = function template(locals) {var buf = [];" +
                "var jade_mixins = {};buf.push(\"<p>Template 2 for multiple testing</p>\");" +
                ";return buf.join(\"\");};\n" +
                "\n" +
                "window.templates.template1 = function template(locals) {var buf = [];var jade_mixins = {};" +
                "buf.push(\"<h1>Template 1 for multiple testing</h1>\");;return buf.join(\"\");};",
                new String(response.getContent()));
    }

    @Test
    public void useCache()
    {
        JadeCompilerImpl compiler = (JadeCompilerImpl) injector.getInstance(JadeCompiler.class);

        JadeCompilerImpl spy = Mockito.spy(compiler);

        spy.process("/jade/cache.jade", JadeCompilerCfg.build());
        spy.process("/jade/cache.jade", JadeCompilerCfg.build());

        Mockito.verify(spy, Mockito.times(1)).compile(Mockito.<FluentResource>any(), Mockito.<JadeCompilerCfg>any());
    }
}


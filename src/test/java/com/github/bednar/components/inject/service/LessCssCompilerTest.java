package com.github.bednar.components.inject.service;

import com.github.bednar.base.utils.resource.FluentResource;
import com.github.bednar.components.AbstractComponentTest;
import com.github.bednar.components.inject.service.resource.ResourceProcessor;
import com.github.bednar.components.inject.service.resource.ResourceResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.JavaScriptException;

/**
 * @author Jakub Bednář (29/12/2013 11:35)
 */
public class LessCssCompilerTest extends AbstractComponentTest
{
    @Test
    public void serviceNotNull()
    {
        LessCssCompiler compiler = injector.getInstance(LessCssCompiler.class);

        Assert.assertNotNull(compiler);
    }

    @Test
    public void compileByPath()
    {
        LessCssCompiler compiler = injector.getInstance(LessCssCompiler.class);

        String compiled = compiler.compile("/less/basic.less");

        Assert.assertEquals(".class {\n  width: 2;\n}\n", compiled);
    }

    @Test
    public void compileByURL()
    {
        LessCssCompiler compiler = injector.getInstance(LessCssCompiler.class);

        String compiled = compiler.compile(this.getClass().getResource("/less/basic.less"));

        Assert.assertEquals(".class {\n  width: 2;\n}\n", compiled);
    }

    @Test(expected = JavaScriptException.class)
    public void compileError()
    {
        LessCssCompiler compiler = injector.getInstance(LessCssCompiler.class);

        compiler.compile("/less/error.less");
    }

    @Test
    public void acceptedType()
    {
        ResourceProcessor processor = injector.getInstance(LessCssCompiler.class);

        Assert.assertTrue(processor.isAcceptedType("/less/basic.less"));
    }

    @Test
    public void notAcceptedType()
    {
        ResourceProcessor processor = injector.getInstance(LessCssCompiler.class);

        Assert.assertFalse(processor.isAcceptedType("/coffee/basic.coffee"));
    }

    @Test
    public void processExistResource()
    {
        ResourceProcessor processor = injector.getInstance(LessCssCompiler.class);

        ResourceResponse response = processor.process("/less/remote.less", false);

        Assert.assertNotNull(response);
        Assert.assertEquals((Object) 31, response.getContentLength());
        Assert.assertEquals("text/css;charset=UTF-8", response.getContentType());
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        Assert.assertEquals("p .for-remote-test{margin:10px}", new String(response.getContent()));
    }

    @Test
    public void processNotExistResource()
    {
        ResourceProcessor processor = injector.getInstance(LessCssCompiler.class);

        ResourceResponse response = processor.process("/less/notexist.less", false);

        Assert.assertNotNull(response);
        Assert.assertEquals((Object) 44, response.getContentLength());
        Assert.assertEquals("text/css;charset=UTF-8", response.getContentType());
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        Assert.assertEquals("// Resource: '/less/notexist.less' not exist", new String(response.getContent()));
    }

    @Test
    public void useCache()
    {
        LessCssCompilerImpl compiler = (LessCssCompilerImpl) injector.getInstance(LessCssCompiler.class);

        LessCssCompilerImpl spy = Mockito.spy(compiler);

        spy.process("/less/cache.less", false);
        spy.process("/less/cache.less", false);

        try (FluentResource resource = FluentResource.byPath("/less/cache.less"))
        {
            Mockito.verify(spy, Mockito.times(1)).compile(resource, true);
        }
    }
}

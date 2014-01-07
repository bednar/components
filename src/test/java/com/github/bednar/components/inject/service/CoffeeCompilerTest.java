package com.github.bednar.components.inject.service;

import com.github.bednar.base.utils.resource.FluentResource;
import com.github.bednar.components.AbstractComponentTest;
import com.github.bednar.components.inject.service.resource.ResourceResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.JavaScriptException;

/**
 * @author Jakub Bednář (30/12/2013 17:13)
 */
public class CoffeeCompilerTest extends AbstractComponentTest
{
    @Test
    public void serviceNotNull()
    {
        CoffeeCompiler compiler = injector.getInstance(CoffeeCompiler.class);

        Assert.assertNotNull(compiler);
    }

    @Test
    public void compileByPath()
    {
        CoffeeCompiler compiler = injector.getInstance(CoffeeCompiler.class);

        String compiled = compiler.compile("/coffee/basic.coffee");

        Assert.assertEquals(
                "(function() {\n" +
                        "  var math;\n" +
                        "\n" +
                        "  math = {\n" +
                        "    root: Math.sqrt,\n" +
                        "    square: square,\n" +
                        "    cube: function(x) {\n" +
                        "      return x * square(x);\n" +
                        "    }\n" +
                        "  };\n" +
                        "\n" +
                        "}).call(this);\n", compiled);
    }

    @Test
    public void compileByURL()
    {
        CoffeeCompiler compiler = injector.getInstance(CoffeeCompiler.class);

        String compiled = compiler.compile(this.getClass().getResource("/coffee/basic.coffee"));

        Assert.assertEquals(
                "(function() {\n" +
                        "  var math;\n" +
                        "\n" +
                        "  math = {\n" +
                        "    root: Math.sqrt,\n" +
                        "    square: square,\n" +
                        "    cube: function(x) {\n" +
                        "      return x * square(x);\n" +
                        "    }\n" +
                        "  };\n" +
                        "\n" +
                        "}).call(this);\n", compiled);
    }

    @Test(expected = JavaScriptException.class)
    public void compileError()
    {
        CoffeeCompiler compiler = injector.getInstance(CoffeeCompiler.class);

        compiler.compile("/coffee/error.coffee");
    }

    @Test
    public void acceptedType()
    {
        CoffeeCompiler processor = injector.getInstance(CoffeeCompiler.class);

        Assert.assertTrue(processor.isAcceptedType("/coffee/basic.coffee"));
    }

    @Test
    public void notAcceptedType()
    {
        CoffeeCompiler processor = injector.getInstance(CoffeeCompiler.class);

        Assert.assertFalse(processor.isAcceptedType("/less/basic.less"));
    }

    @Test
    public void processExistResource()
    {
        CoffeeCompiler processor = injector.getInstance(CoffeeCompiler.class);

        ResourceResponse response = processor.process("/coffee/remote.coffee", CoffeeCompilerCfg.build());

        Assert.assertNotNull(response);
        Assert.assertEquals((Object) 73, response.getContentLength());
        Assert.assertEquals("application/javascript;charset=UTF-8", response.getContentType());
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        Assert.assertEquals(
                "(function() {\n  var date;\n\n  date = friday ? sue : jill;\n\n}).call(this);\n",
                new String(response.getContent()));
    }

    @Test
    public void processNotExistResource()
    {
        CoffeeCompiler processor = injector.getInstance(CoffeeCompiler.class);

        ResourceResponse response = processor.process("/coffee/notexist.coffee", CoffeeCompilerCfg.build());

        Assert.assertNotNull(response);
        Assert.assertEquals((Object) 48, response.getContentLength());
        Assert.assertEquals("application/javascript;charset=UTF-8", response.getContentType());
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        Assert.assertEquals("// Resource: '/coffee/notexist.coffee' not exist", new String(response.getContent()));
    }

    @Test
    public void useCache()
    {
        CoffeeCompilerImpl compiler = (CoffeeCompilerImpl) injector.getInstance(CoffeeCompiler.class);

        CoffeeCompilerImpl spy = Mockito.spy(compiler);

        spy.process("/coffee/cache.coffee", CoffeeCompilerCfg.build());
        spy.process("/coffee/cache.coffee", CoffeeCompilerCfg.build());

        Mockito.verify(spy, Mockito.times(1)).compile(Mockito.<FluentResource>any(), Mockito.<CoffeeCompilerCfg>any());
    }
}

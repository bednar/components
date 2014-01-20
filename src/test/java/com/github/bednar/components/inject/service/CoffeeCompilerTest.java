package com.github.bednar.components.inject.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.github.bednar.base.utils.resource.FluentResource;
import com.github.bednar.components.AbstractComponentTest;
import com.github.bednar.components.inject.service.resource.ResourceResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.JavaScriptException;

import static com.github.bednar.components.Defaults.WAIT_FOR_CHANGE;

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
    public void parallelCompile() throws InterruptedException, ExecutionException
    {
        int threadCount = 5;

        final CoffeeCompiler compiler = injector.getInstance(CoffeeCompiler.class);

        Callable<String> callable = new Callable<String>()
        {
            public String call() throws Exception
            {
                return compiler.compile("/coffee/basic.coffee");
            }
        };

        List<Future<String>> futures = Executors
                .newFixedThreadPool(threadCount)
                .invokeAll(Collections.nCopies(threadCount, callable));

        for (Future<String> future : futures)
        {
            future.get();
        }
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

    @Test
    public void correctContentChangedFiles() throws Exception
    {
        CoffeeCompilerImpl processor = (CoffeeCompilerImpl) injector.getInstance(CoffeeCompiler.class);

        CoffeeCompilerCfg cfg = CoffeeCompilerCfg
                .build()
                .setBare(true);

        ResourceResponse response = processor.process("/coffee/contentChange.coffee", cfg);

        Assert.assertEquals(
                "if (typeof elvis !== \"undefined\" && elvis !== null) {\n  alert(\"I knew it!\");\n}\n",
                new String(response.getContent()));

        try (FluentResource resource = FluentResource.byPath("/coffee/contentChange.coffee"))
        {
            resource.update("### \nsome coffee comments\n###");
        }

        Thread.sleep(WAIT_FOR_CHANGE);

        response = processor.process("/coffee/contentChange.coffee", cfg);

        Assert.assertEquals("/* \nsome coffee comments\n*/\n\n\n", new String(response.getContent()));
    }
}

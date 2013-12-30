package com.github.bednar.components.inject.service;

import com.github.bednar.components.AbstractComponentTest;
import org.junit.Assert;
import org.junit.Test;
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
}

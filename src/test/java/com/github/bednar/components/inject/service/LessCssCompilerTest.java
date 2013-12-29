package com.github.bednar.components.inject.service;

import com.github.bednar.components.AbstractComponentTest;
import org.junit.Assert;
import org.junit.Test;

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
}

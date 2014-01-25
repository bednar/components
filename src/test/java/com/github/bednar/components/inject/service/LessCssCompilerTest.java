package com.github.bednar.components.inject.service;

import com.github.bednar.base.utils.resource.FluentResource;
import com.github.bednar.components.AbstractComponentTest;
import com.github.bednar.components.inject.service.resource.ResourceResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.JavaScriptException;

import static com.github.bednar.components.Defaults.WAIT_FOR_CHANGE;

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

        Assert.assertEquals(".class{width:2}", compiled);
    }

    @Test
    public void compileByURL()
    {
        LessCssCompiler compiler = injector.getInstance(LessCssCompiler.class);

        String compiled = compiler.compile(this.getClass().getResource("/less/basic.less"));

        Assert.assertEquals(".class{width:2}", compiled);
    }

    @Test
    public void compileWithImport()
    {
        LessCssCompiler compiler = injector.getInstance(LessCssCompiler.class);

        String compiled = compiler.compile("/less/withimport.less");

        Assert.assertEquals(
                "div p{padding-top:5px;padding-right:10px;padding-bottom:15px;padding-left:20px;" +
                "color:#f00;background-color:#f00}" +
                "div p .show{padding-top:3px;padding-right:6px;padding-bottom:9px;padding-left:12px;" +
                "color:#ffc0cb;background-color:#ffc0cb}", compiled);
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
        LessCssCompiler processor = injector.getInstance(LessCssCompiler.class);

        Assert.assertTrue(processor.isAcceptedType("/less/basic.less"));
    }

    @Test
    public void notAcceptedType()
    {
        LessCssCompiler processor = injector.getInstance(LessCssCompiler.class);

        Assert.assertFalse(processor.isAcceptedType("/coffee/basic.coffee"));
    }

    @Test
    public void processExistResource()
    {
        LessCssCompiler processor = injector.getInstance(LessCssCompiler.class);

        ResourceResponse response = processor.process("/less/remote.less", LessCssCompilerCfg.build());

        Assert.assertNotNull(response);
        Assert.assertEquals((Object) 31, response.getContentLength());
        Assert.assertEquals("text/css;charset=UTF-8", response.getContentType());
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        Assert.assertEquals("p .for-remote-test{margin:10px}", new String(response.getContent()));
    }

    @Test
    public void processNotExistResource()
    {
        LessCssCompiler processor = injector.getInstance(LessCssCompiler.class);

        ResourceResponse response = processor.process("/less/notexist.less", LessCssCompilerCfg.build());

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

        spy.process("/less/cache.less", LessCssCompilerCfg.build());
        spy.process("/less/cache.less", LessCssCompilerCfg.build());

        Mockito.verify(spy, Mockito.times(1)).compile(Mockito.<FluentResource>any(), Mockito.<LessCssCompilerCfg>any());
    }

    @Test
    public void correctContentChangedFiles() throws Exception
    {
        LessCssCompilerImpl processor = (LessCssCompilerImpl) injector.getInstance(LessCssCompiler.class);

        ResourceResponse response = processor.process("/less/contentChange.less", LessCssCompilerCfg.build());

        Assert.assertEquals("span{color:deeppink}", new String(response.getContent()));

        try (FluentResource resource = FluentResource.byPath("/less/contentChange.less"))
        {
            resource.update("span{\n\tcolor:blue\n}");
        }

        Thread.sleep(WAIT_FOR_CHANGE);

        response = processor.process("/less/contentChange.less", LessCssCompilerCfg.build());

        Assert.assertEquals("span{color:#00f}", new String(response.getContent()));
    }

    @Test
    public void correctContentChangedFilesWithImport() throws InterruptedException
    {
        LessCssCompilerImpl processor = (LessCssCompilerImpl) injector.getInstance(LessCssCompiler.class);

        ResourceResponse response = processor.process("/less/withimportForChange.less", LessCssCompilerCfg.build());

        Assert.assertEquals(".file-for-change{font-weight:bold}.with-import{color:blue}", new String(response.getContent()));

        try (FluentResource resource = FluentResource.byPath("/less/sub/subForChange.less"))
        {
            resource.update("span{\n\tcolor:blue\n}");
        }

        Thread.sleep(WAIT_FOR_CHANGE);

        response = processor.process("/less/withimportForChange.less", LessCssCompilerCfg.build());

        Assert.assertEquals("span{color:#00f}.with-import{color:blue}", new String(response.getContent()));
    }
}

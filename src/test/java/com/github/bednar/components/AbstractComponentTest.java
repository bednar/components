package com.github.bednar.components;

import javax.annotation.Nonnull;

import com.github.bednar.base.http.AppContext;
import com.github.bednar.base.inject.Injector;
import com.github.bednar.test.EmbeddedJetty;
import com.google.common.base.Preconditions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Jakub Bednář (17/12/2013 18:07)
 */
public abstract class AbstractComponentTest
{
    /**
     * Class scope
     */
    protected static EmbeddedJetty embeddedJetty;

    protected Injector injector;

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        embeddedJetty = new EmbeddedJetty()
                .webFragments(true)
                .start();

        AppContext.initInjector(embeddedJetty.getServletContext());
    }

    @Before
    public void before()
    {
        injector = AppContext.getInjector();
    }

    @After
    public void after()
    {
    }

    @AfterClass
    public static void afterClass() throws Exception
    {
        AppContext
                .clear();

        embeddedJetty
                .stop();
    }

    @Nonnull
    protected String getResourcePath()
    {
        return "";
    }

    @Nonnull
    protected String urlPath()
    {
        return urlPath(getResourcePath());
    }

    @Nonnull
    protected String urlPath(@Nonnull final String resource)
    {
        Preconditions.checkNotNull(resource);

        return embeddedJetty.getURL() + "api/" + resource;
    }
}
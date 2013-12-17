package com.github.bednar.components.api;

import javax.annotation.Nonnull;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import java.util.concurrent.ExecutionException;

import com.github.bednar.components.AbstractComponentTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (17/12/2013 18:10)
 */
public class LocalizationTest extends AbstractComponentTest
{
    @Nonnull
    @Override
    protected String getResourcePath()
    {
        return "localization";
    }

    @Test
    public void isNotAuthenticated() throws ExecutionException, InterruptedException
    {
        Response response = ClientBuilder.newClient()
                .target(urlPath())
                .request("application/json")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, response.getStatus());
    }
}

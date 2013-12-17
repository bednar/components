package com.github.bednar.components.api;

import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.bednar.base.api.ApiResource;
import com.github.bednar.base.utils.collection.ListAutoCloseable;
import com.github.bednar.base.utils.reflection.FluentReflection;
import com.github.bednar.base.utils.resource.FluentResource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (17/12/2013 17:35)
 */
@Path("localization")
@Consumes("application/json")
@Produces("application/json")
@Api(value = "Localization", description = "Localized texts used in application.", position = 5)
public class Localization implements ApiResource
{
    private static final Logger LOG = LoggerFactory.getLogger(Localization.class);

    private final Map<String, String> texts = Maps.newConcurrentMap();

    public Localization() throws ConfigurationException
    {
        CompositeConfiguration config = new CompositeConfiguration();

        for (String resourcePath : findLocalizationResources())
        {
            LOG.info("[localization-file][{}]", resourcePath);

            config.addConfiguration(new PropertiesConfiguration(resourcePath));
        }

        Iterator<String> keys = config.getKeys();
        while (keys.hasNext())
        {
            String key      =  keys.next();
            String value    = config.getString(key);

            LOG.info("[localization-value][{}][{}]", key, value);

            texts.put(key, value);
        }
    }

    @GET
    @ApiOperation(position = 1, value = "All localized texts")
    @ApiResponse(code = 200, message = "{}")
    public void get(@Nonnull @Suspend final AsynchronousResponse response)
    {
        response.setResponse(Response.ok(texts).build());
    }

    @Nonnull
    private Set<String> findLocalizationResources()
    {
        return FluentReflection
                .forPackage("localization")
                .getResources(Pattern.compile(".*\\.properties"));
    }
}

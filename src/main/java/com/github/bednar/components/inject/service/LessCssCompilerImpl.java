package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import javax.cache.Cache;
import java.util.regex.Pattern;

import com.github.bednar.base.utils.cache.FluentCache;
import com.github.bednar.base.utils.resource.FluentResource;
import com.github.bednar.components.inject.service.resource.ResourceResponse;

/**
 * @author Jakub Bednář (29/12/2013 11:32)
 */
public class LessCssCompilerImpl extends AbstractJavascriptCompiler implements LessCssCompiler
{
    private final Pattern pattern = Pattern.compile(".*\\.less");
    private final Cache<String, ResourceResponse> cache;

    public LessCssCompilerImpl()
    {
        super("/lib/less.min.js");

        cache = FluentCache.cacheByClass(LessCssCompilerImpl.class, String.class, ResourceResponse.class);
    }

    @Nonnull
    protected final String compile(@Nonnull final FluentResource lessResource, @Nonnull final Boolean compress)
    {
        String lessPath     = lessResource.path();
        String lessContent  = lessResource.asString().replaceAll("\\s", " ");

        String script = String.format(
                "var result;" +

                "new less.Parser({filename: '%s'}).parse('%s', function (error, less)" +
                "{" +
                "   result = less.toCSS({ compress: %s});" +
                "});" +

                "result;", lessPath, lessContent, compress);

        return evaluateInline(lessPath, script);
    }

    @Nonnull
    @Override
    public Boolean isAcceptedType(@Nonnull final String resourcePath)
    {
        return pattern.matcher(resourcePath).matches();
    }

    @Nonnull
    @Override
    public ResourceResponse process(@Nonnull final String resourcePath, @Nonnull final Boolean pretty)
    {
        try (FluentResource resource = FluentResource.byPath(resourcePath))
        {
            if (cache.containsKey(resource.path()))
            {
                return cache.get(resource.path());
            }

            if (resource.exists())
            {
                String content = compile(resource, !pretty);

                ResourceResponse response = build(content, "text/css");

                cache.put(resource.path(), response);

                return response;
            }
            else
            {
                String content = String.format("// Resource: '%s' not exist", resourcePath);

                return build(content, "text/css");
            }
        }
    }
}

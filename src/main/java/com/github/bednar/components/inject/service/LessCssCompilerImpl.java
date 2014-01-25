package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.bednar.base.utils.resource.FluentResource;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (29/12/2013 11:32)
 */
public class LessCssCompilerImpl extends AbstractJavascriptCompiler<LessCssCompilerCfg> implements LessCssCompiler
{
    private static final Logger LOG = LoggerFactory.getLogger(LessCssCompilerImpl.class);

    private static final Pattern IMPORT_PATTERN = Pattern
            .compile("(@import\\s*)(\"|')(\\S*)(\"|');", Pattern.CASE_INSENSITIVE);

    public LessCssCompilerImpl()
    {
        super("/lib/less.min.js");
    }

    @Nonnull
    @Override
    protected String resourceRegexp()
    {
        return ".*\\.less.*";
    }

    @Nonnull
    @Override
    protected String contentType(@Nonnull final LessCssCompilerCfg cfg)
    {
        return "text/css";
    }

    @Nonnull
    @Override
    public LessCssCompilerCfg defaultCfg(@Nonnull final Map<String, String[]> parameters)
    {
        return LessCssCompilerCfg.build();
    }

    @Nonnull
    @Override
    protected String compile(@Nonnull final FluentResource lessResource, @Nonnull final LessCssCompilerCfg cfg)
    {
        String lessPath     = lessResource.path();
        String lessContent  = replaceImports(lessResource);

        String script = String.format(
                "var result;" +

                "new less.Parser({filename: '%s'}).parse(content, function (error, less)" +
                "{" +
                "   result = less.toCSS({ compress: %s});" +
                "});" +

                "result;", lessPath, cfg.getCompress());

        return evaluateInline(lessPath, script, lessContent);
    }

    @Nonnull
    @Override
    protected Set<Path> resourcePaths(@Nonnull final FluentResource resource)
    {
        Set<Path> results = Sets.newHashSet();
        results.add(resource.asPath());

        Matcher matcher = IMPORT_PATTERN.matcher(resource.asString());

        while (matcher.find())
        {
            String importPath = matcher.group(3);

            try (FluentResource importResource = FluentResource.byPath(resource.directory() + importPath))
            {
                results.addAll(resourcePaths(importResource));
            }
        }

        return results;
    }

    @Nonnull
    private String replaceImports(@Nonnull final FluentResource resource)
    {
        return replaceImports(resource.asString(), resource.directory());
    }

    @Nonnull
    private String replaceImports(@Nonnull final String content, @Nonnull final String basePath)
    {
        Matcher matcher = IMPORT_PATTERN.matcher(content);

        if (matcher.find())
        {
            String importPath = matcher.group(3);

            try (FluentResource importResource = FluentResource.byPath(basePath + importPath))
            {
                LOG.info("[import-from][{}]", importResource.path());

                String importContent = replaceImports(importResource);

                String replacedContent = matcher.replaceFirst(importContent);

                //process next @import
                return replaceImports(replacedContent, basePath);
            }
        }
        else
        {
            return content;
        }
    }
}

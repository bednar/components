package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.bednar.base.utils.collection.ListAutoCloseable;
import com.github.bednar.base.utils.lang.Patterns;
import com.github.bednar.base.utils.resource.FluentResource;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jakub Bednář (29/12/2013 18:16)
 */
public class JadeCompilerImpl extends AbstractJavascriptCompiler<JadeCompilerCfg> implements JadeCompiler
{
    public JadeCompilerImpl()
    {
        super("/lib/trimLeft.js", "/lib/jade/jade.min.js");
    }

    @Nonnull
    @Override
    protected String resourceRegexp()
    {
        return ".*\\.jade";
    }

    @Nonnull
    @Override
    protected String contentType(@Nonnull final JadeCompilerCfg cfg)
    {
        if (cfg.getAsHTML())
        {
            return "text/html";
        }
        else
        {
            return "application/javascript";
        }
    }

    @Nonnull
    @Override
    public JadeCompilerCfg defaultCfg(@Nonnull final Map<String, String[]> parameters)
    {
        return JadeCompilerCfg.build(parameters);
    }

    @Nonnull
    @Override
    protected String compile(@Nonnull final FluentResource resource, @Nonnull final JadeCompilerCfg cfg)
    {
        String jadePath     = resource.path();
        String jadeContent  = resource.asString();

        String options  = String.format("{filename: '%s', pretty: %s, client: true}", jadePath, cfg.getPretty());

        if (cfg.getAsHTML())
        {
            String script = String.format("jade.render(content, %s);", options);

            return evaluateInline(jadePath, script, jadeContent);
        }
        else if (cfg.hasMultiple())
        {
            return evaluateMultiple(cfg);
        }
        else if (cfg.hasAssignTo())
        {
            String compiled = evaluateRuntimeScript(jadePath, jadeContent, options);

            Matcher matcher = Patterns.FILE_NAME_EXTENSION.matcher(jadePath);
            matcher.find();

            return String.format("%s.%s = %s;", cfg.getAssignTo(), matcher.group(2), compiled);
        }
        else
        {
            return evaluateRuntimeScript(jadePath, jadeContent, options);
        }
    }

    @Nonnull
    @Override
    protected List<String> cacheKeyParameters(@Nonnull final JadeCompilerCfg cfg)
    {
        List<String> results = Lists.newArrayList();

        if (cfg.hasAssignTo())
        {
            results.add(cfg.getAssignTo());
        }

        if (cfg.hasMultiple())
        {
            results.add(cfg.getMultiple());
        }

        return results;
    }

    @Nonnull
    @Override
    protected Boolean existResource(@Nonnull final FluentResource resource, @Nonnull final JadeCompilerCfg cfg)
    {
        return cfg.hasMultiple() || resource.exists();
    }

    @Nonnull
    private String evaluateRuntimeScript(@Nonnull final String jadePath,
                                         @Nonnull final String jadeContent,
                                         @Nonnull final String options)
    {
        String script = String.format("'' + jade.compile(content, %s);", options);

        return evaluateInline(jadePath, script, jadeContent).replaceAll("\n", "").replaceAll("\\\\\"", "\"");
    }

    @Nonnull
    private String evaluateMultiple(@Nonnull final JadeCompilerCfg cfg)
    {
        List<String> results = Lists.newArrayList();

        Pattern pattern = Pattern.compile(cfg.getMultiple());

        try (ListAutoCloseable<FluentResource> resources = FluentResource.byPattern("jade", pattern))
        {
            for (FluentResource resource : resources)
            {
                String compile = compile(resource, JadeCompilerCfg.build().setAssignTo(cfg.getAssignTo()));

                results.add(compile);
            }
        }

        return StringUtils.join(results, "\n\n");
    }
}

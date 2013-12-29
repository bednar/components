function compileJade(fileName, content, pretty)
{
    var options = {filename: fileName, pretty: pretty, client: true};

    return '' + jade.compile(content, options);
}
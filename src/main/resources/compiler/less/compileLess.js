function compileLess(fileName, content, compress)
{
    var result;

    var parser = new less.Parser(
        {
            filename: fileName
        });

    parser.parse(content, function (error, less)
    {
        result = less.toCSS({ compress: compress });
    });

    return result;
}
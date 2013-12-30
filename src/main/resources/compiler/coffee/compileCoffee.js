function compileCoffee(content)
{
    var options = {bare: false};

    return '' + CoffeeScript.compile(content, options);
}
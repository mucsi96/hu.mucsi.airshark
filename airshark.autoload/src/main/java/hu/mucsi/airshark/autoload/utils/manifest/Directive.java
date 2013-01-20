package hu.mucsi.airshark.autoload.utils.manifest;

public class Directive
{

    private final String name;
    private final String value;

    public Directive(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

}

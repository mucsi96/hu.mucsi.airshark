package hu.mucsi.airshark.autoload.utils.manifest;

public class Clause
{

    private final String name;
    private final Directive[] directives;
    private final Attribute[] attributes;

    public Clause(String name, Directive[] directives, Attribute[] attributes)
    {
        this.name = name;
        this.directives = directives;
        this.attributes = attributes;
    }

    public String getName()
    {
        return name;
    }

    public Directive[] getDirectives()
    {
        return directives;
    }

    public Attribute[] getAttributes()
    {
        return attributes;
    }

    public String getDirective(String name)
    {
        for (int i = 0; i < directives.length; i++)
        {
            if (name.equals(directives[i].getName()))
            {
                return directives[i].getValue();
            }
        }
        return null;
    }

    public String getAttribute(String name)
    {
        for (int i = 0; i < attributes.length; i++)
        {
            if (name.equals(attributes[i].getName()))
            {
                return attributes[i].getValue();
            }
        }
        return null;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(name);
        for (int i = 0; directives != null && i < directives.length; i++)
        {
            sb.append(";").append(directives[i].getName()).append(":=");
            if (directives[i].getValue().indexOf(",") >= 0)
            {
                sb.append("\"").append(directives[i].getValue()).append("\"");
            }
            else
            {
                sb.append(directives[i].getValue());
            }
        }
        for (int i = 0; attributes != null && i < attributes.length; i++)
        {
            sb.append(";").append(attributes[i].getName()).append("=");
            if (attributes[i].getValue().indexOf(",") >= 0)
            {
                sb.append("\"").append(attributes[i].getValue()).append("\"");
            }
            else
            {
                sb.append(attributes[i].getValue());
            }
        }
        return sb.toString();
    }
}

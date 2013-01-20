package hu.mucsi.airshark.autoload.utils.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionCleaner {

    private VersionCleaner() { }


    private static final Pattern FUZZY_VERSION = Pattern.compile("(\\d+)(\\.(\\d+)(\\.(\\d+))?)?([^a-zA-Z0-9](.*))?", Pattern.DOTALL);

    /**
     * Clean up version parameters. Other builders use more fuzzy definitions of
     * the version syntax. This method cleans up such a version to match an OSGi
     * version.
     *
     * @param version
     * @return
     */
    public static String clean(String version)
    {
        if (version == null || version.length() == 0)
        {
            return "0.0.0";
        }
        StringBuffer result = new StringBuffer();
        Matcher m = FUZZY_VERSION.matcher(version);
        if (m.matches())
        {
            String major = m.group(1);
            String minor = m.group(3);
            String micro = m.group(5);
            String qualifier = m.group(7);

            if (major != null)
            {
                result.append(major);
                if (minor != null)
                {
                    result.append(".");
                    result.append(minor);
                    if (micro != null)
                    {
                        result.append(".");
                        result.append(micro);
                        if (qualifier != null)
                        {
                            result.append(".");
                            cleanupModifier(result, qualifier);
                        }
                    }
                    else if (qualifier != null)
                    {
                        result.append(".0.");
                        cleanupModifier(result, qualifier);
                    }
                    else
                    {
                        result.append(".0");
                    }
                }
                else if (qualifier != null)
                {
                    result.append(".0.0.");
                    cleanupModifier(result, qualifier);
                }
                else
                {
                    result.append(".0.0");
                }
            }
        }
        else
        {
            result.append("0.0.0.");
            cleanupModifier(result, version);
        }
        return result.toString();
    }

    private static void cleanupModifier(StringBuffer result, String modifier) {
        for (int i = 0; i < modifier.length(); i++) {
            char c = modifier.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z') || c == '_' || c == '-')
                result.append(c);
            else
                result.append('_');
        }
    }

}

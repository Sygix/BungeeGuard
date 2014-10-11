package net.uhcwork.BungeeGuard.Utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Part of net.uhcwork.BungeeGuard.Utils (BungeeGuard)
 * Date: 11/10/2014
 * Time: 17:55
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class StringTemplate {

    static final private Pattern keyPattern =
            Pattern.compile("\\$\\{([a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)*)\\}");
    final private String template;
    final private Matcher m;
    private boolean blanknull = false;

    public StringTemplate(String template) {
        this.template = template;
        this.m = keyPattern.matcher(template);
        setBlankNull();
    }

    /**
     * @param map substitution map
     * @return substituted string
     */
    public String substitute(Map<String, String> map) {
        this.m.reset();
        StringBuffer sb = new StringBuffer();
        while (this.m.find()) {
            String k0 = this.m.group();
            String k = this.m.group(1);
            Object vobj = map.get(k);
            String v = (vobj == null)
                    ? (this.blanknull ? "" : k0)
                    : vobj.toString();
            this.m.appendReplacement(sb, Matcher.quoteReplacement(v));
        }
        this.m.appendTail(sb);
        return sb.toString();
    }

    public StringTemplate setBlankNull() {
        this.blanknull = true;
        return this;
    }
}
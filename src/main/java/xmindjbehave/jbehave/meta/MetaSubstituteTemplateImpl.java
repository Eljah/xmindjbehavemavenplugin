package xmindjbehave.jbehave.meta;

/**
 * Created by Ilya Evlampiev on 24.02.15.
 */
public class MetaSubstituteTemplateImpl implements MetaTemplate {
    @Override
    public void validateTemplate() {

    }

    @Override
    public String parseMeta(String string) {
        Substitute sub = new Substitute();

        int index_of_keywordstart = -1;
        int index_of_bound1 = -1;
        int index_of_comma = -1;
        int index_of_bound2 = -1;

        String key;
        String value;

        index_of_keywordstart = string.indexOf("${");
        if (index_of_keywordstart != -1) {
            index_of_bound1 = index_of_keywordstart + 1;

            String rest = string.substring(index_of_bound1);
            for (char coursor : rest.toCharArray()) {
                if (coursor == ':') {
                    index_of_comma = rest.indexOf(coursor);
                    break;
                }
            }
            for (char coursor : rest.toCharArray()) {
                if (coursor == '}') {
                    index_of_bound2 = rest.indexOf(coursor);
                    break;
                }
            }

            sub.key = rest.substring(1, index_of_comma);
            sub.value = rest.substring(index_of_comma + 1, index_of_bound2);

            index_of_bound2 = index_of_bound1 + index_of_bound2;
            index_of_comma = index_of_bound1 + index_of_comma;

            String toRespond = string.replace("${" + sub.key + ":" + sub.value + "}", sub.value);

            while (toRespond.indexOf("${" + sub.key + "}") != -1) {
                toRespond = toRespond.replace("${" + sub.key + "}", sub.value);
            }
            toRespond=parseMeta(toRespond);
            toRespond=toRespond.replaceAll("\r\n\r\n","\r\n");

            return toRespond;
        } else
            return string;
    }
}

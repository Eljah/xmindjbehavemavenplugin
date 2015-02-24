package xmindjbehave.jbehave.meta;

/**
 * Created by Ilya Evlampiev on 24.02.15.
 */
public class MetaCopyPasteTemplateImpl implements MetaTemplate {
    @Override
    public void validateTemplate() {

    }

    @Override
    public String parseMeta(String string) {
        Substitute sub = new Substitute();

        int index_of_keywordstart = -1;
        int index_of_bound1 = -1;
        int index_of_bound2 = -1;

        String value;

        index_of_keywordstart = string.indexOf("${:");
        if (index_of_keywordstart != -1) {
            index_of_bound1 = index_of_keywordstart + 2;

            String rest = string.substring(index_of_bound1);

            for (char coursor : rest.toCharArray()) {
                if (coursor == '}') {
                    index_of_bound2 = rest.indexOf(coursor);
                    break;
                }
            }

            sub.value = rest.substring(1, index_of_bound2);

            index_of_bound2 = index_of_bound1 + index_of_bound2;

            String toRespond = string.replace("${:"+sub.value + "}", sub.value);

            while (toRespond.indexOf("${:}") != -1) {
                toRespond = toRespond.replace("${:}", sub.value);
            }
            toRespond=parseMeta(toRespond);
            toRespond=toRespond.replaceAll("\r\n\r\n","\r\n");

            return toRespond;
        } else
            return string;
    }
}

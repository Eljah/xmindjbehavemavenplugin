package xmindjbehave.jbehave.meta;

import xmindjbehave.jbehave.MetaLanguageStatements;

/**
 * Created by Ilya Evlampiev on 22.02.15.
 */
public interface MetaTemplate {

    public void validateTemplate();

    public String parseMeta(String string);
}

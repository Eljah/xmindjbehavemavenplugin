package xmindjbehave.jbehave.meta;

import xmindjbehave.jbehave.MetaLanguageStatements;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ilya Evlampiev on 24.02.15.
 */
public class MetaIntegerWithIncrement extends AbstractMetaWithIncrement implements MetaWithIncrement {
    public int currentVal;

    MetaIntegerWithIncrement() {
        name = MetaLanguageStatements.IntIncrementRange;
        currentVal = val1;
    }

    @Override
    public Integer processIncrementInclIncl() {

        for (int i = val1; i <= val2; i = i + increment) {
            this.values.add(i);
            currentVal=i;
        }
        return currentVal;
    }

    @Override
    public Integer processIncrementInclExcl() {
        for (int i = val1; i < val2; i = i + increment) {
            this.values.add(i);
            currentVal=i;
        }
        return currentVal;
    }

    @Override
    public Integer processIncrementExclIncl() {
        for (int i = val1+increment; i <= val2; i = i + increment) {
            this.values.add(i);
            currentVal=i;
        }
        return currentVal;
    }

    @Override
    public Integer processIncrementExclExcl() {
        for (int i = val1+increment; i < val2; i = i + increment) {
            this.values.add(i);
            currentVal=i;
        }
        return currentVal;
    }

    @Override
    public ArrayList<Integer> getValues() {
        return values;
    }

}

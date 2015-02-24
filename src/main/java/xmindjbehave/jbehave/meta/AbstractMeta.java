package xmindjbehave.jbehave.meta;

import xmindjbehave.jbehave.MetaLanguageStatements;

import java.util.ArrayList;

/**
 * Created by Ilya Evlampiev on 22.02.15.
 */
abstract class AbstractMeta implements Meta {
    protected MetaLanguageStatements name;
    protected int val1;
    protected int val2;
    protected Bound bound1;
    protected Bound bound2;
    protected ArrayList<Integer> values = new ArrayList<Integer>() {
    };

    public Integer process() {
        if (bound1.isIncluded() && bound2.isIncluded()) {
            return processIncrementInclIncl();
        }
        if (bound1.isIncluded() && !bound2.isIncluded()) {
            return processIncrementInclExcl();
        }
        if (!bound1.isIncluded() && bound2.isIncluded()) {
            return processIncrementExclIncl();
        }
        if (!bound1.isIncluded() && !bound2.isIncluded()) {
            return processIncrementExclExcl();
        }
        return null;
    }

    @Override
    public int getStartValue() {
        return this.val1;
    }

    @Override
    public int getEndValue() {
        return this.val2;
    }

    @Override
    public void setStartValue(int val1) {
        this.val1 = val1;
    }

    @Override
    public void setEndValue(int val2) {
        this.val2 = val2;
    }

    @Override
    public Bound getStartBound() {
        return this.bound1;
    }

    @Override
    public Bound getEndBound() {
        return this.bound2;
    }

    @Override
    public void setStartBound(Bound bound1) {
        this.bound1 = bound1;
    }

    @Override
    public void setEndBound(Bound bound2) {
        this.bound2 = bound2;
    }

    @Override
    public MetaLanguageStatements getMetaName() {
        return name;
    }

}

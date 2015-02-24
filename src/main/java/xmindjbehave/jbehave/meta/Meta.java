package xmindjbehave.jbehave.meta;

import xmindjbehave.jbehave.MetaLanguageStatements;

import java.util.ArrayList;

/**
 * Created by Ilya Evlampiev on 22.02.15.
 */
public interface Meta<Class> {

    public Integer processIncrementInclIncl();
    public Integer processIncrementInclExcl();
    public Integer processIncrementExclIncl();
    public Integer processIncrementExclExcl();

    public Integer process();
    public ArrayList<Integer> getValues();

    public int getStartValue();
    public int getEndValue();

    public void setStartValue(int val1);
    public void setEndValue(int val2);

    public Bound getStartBound();
    public Bound getEndBound();

    public void setStartBound(Bound bound1);
    public void setEndBound(Bound bound2);

    public MetaLanguageStatements getMetaName();

    public int getThirdValue();

    public void setThirdValue(int val3);

    public void addValues();
}

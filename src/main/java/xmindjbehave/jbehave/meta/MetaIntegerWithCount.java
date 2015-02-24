package xmindjbehave.jbehave.meta;

import xmindjbehave.jbehave.MetaLanguageStatements;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ilya Evlampiev on 22.02.15.
 */
public class MetaIntegerWithCount  extends AbstractMetaWithCount implements MetaWithCount {

    MetaIntegerWithCount()
    {
        name=MetaLanguageStatements.IntCountRange;
    }

    @Override
    public Integer processIncrementInclIncl() {
        Random r = new Random();
        int Low = val1;
        int High = val2;
        //Low++;
        //High--;
        int R;
        if ((High - Low)>0)
        {
            R= (r.nextInt(High - Low)) + Low;
        }
        else {R=Low;}
        return R;
    }

    @Override
    public Integer processIncrementInclExcl() {
        Random r = new Random();
        int Low = val1;
        int High = val2;
        //Low++;
        High--;
        int R;
        if ((High - Low)>0)
        {
            R= (r.nextInt(High - Low)) + Low;
        }
        else {R=Low;}
        return R;
    }

    @Override
    public Integer processIncrementExclIncl() {
        Random r = new Random();
        int Low = val1;
        int High = val2;
        Low++;
        //High--;
        int R;
        if ((High - Low)>0)
        {
            R= (r.nextInt(High - Low)) + Low;
        }
        else {R=Low;}
        return R;
    }

    @Override
    public Integer processIncrementExclExcl() {
        Random r = new Random();
        int Low = val1;
        int High = val2;
        Low++;
        High--;
        int R;
        if ((High - Low)>0)
        {
            R= (r.nextInt(High - Low)) + Low;
        }
        else {R=Low;}
        return R;
    }

    @Override
    public ArrayList<Integer> getValues()
    {
        return values;
    }

}

package xmindjbehave.jbehave.meta;

/**
 * Created by Ilya Evlampiev on 22.02.15.
 */
abstract class AbstractMetaWithCount extends AbstractMeta implements MetaWithCount{
    protected int count;

    @Override
    public int getThirdValue() {
        return this.count;
    }

    @Override
    public void setThirdValue(int count) {
        this.count=count;
    }

    @Override
    public void addValues()

    {
        for (int i=0;i<count;i++)
        {
        this.values.add(process());
        }
    }

}

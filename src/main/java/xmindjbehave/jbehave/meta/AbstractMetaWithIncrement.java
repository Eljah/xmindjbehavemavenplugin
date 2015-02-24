package xmindjbehave.jbehave.meta;

/**
 * Created by Ilya Evlampiev on 22.02.15.
 */
abstract class AbstractMetaWithIncrement extends AbstractMeta implements MetaWithIncrement{
    protected int increment;
    @Override
    public int getThirdValue() {
        return this.increment;
    }

    @Override
    public void setThirdValue(int increment) {
        this.increment=increment;
    }

    @Override
    public void addValues()

    {
        process();
    }
}

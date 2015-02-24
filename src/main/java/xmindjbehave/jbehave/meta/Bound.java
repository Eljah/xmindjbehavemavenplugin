package xmindjbehave.jbehave.meta;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Ilya Evlampiev on 22.02.15.
 */

public class Bound {
    Bound(char bound) {
        if ((bound == '[') || (bound == ']')) {
            included = true;
        } else {
            if ((bound == '(') || (bound == ')')) {
                included = false;
            } else {
                new NotImplementedException();
            }
            ;
        }

    }

    private boolean included;

    public boolean isIncluded() {
        return included;
    }
}

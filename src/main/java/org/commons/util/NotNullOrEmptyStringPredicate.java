
package org.commons.util;

import org.apache.commons.collections.Predicate;

import java.io.Serializable;

public class NotNullOrEmptyStringPredicate implements Predicate, Serializable {

    public static final Predicate INSTANCE = new NotNullOrEmptyStringPredicate();

    public static Predicate getInstance() {
        return INSTANCE;
    }

    private NotNullOrEmptyStringPredicate() {
    }

    public boolean evaluate(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof String)) {
            throw new ClassCastException("Expected String and found " + object.getClass());
        }
        return !StringUtil.isNullOrEmpty((String) object);
    }

}

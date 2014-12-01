package org.commons.util;

import org.commons.specification.Specification;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SpecificationUtil {

    public static Collection/*<String>*/ getClassNamesTerms(Specification specification) {

        Collection result = new ArrayList(specification.getTerms());
        CollectionUtils.transform(result, new Transformer() {
            public Object transform(Object o) {
                return ((Class) o).getName();
            }
        });
        return result;
    }

    public static Collection/*<String>*/ getSpecificationClassesNamesForObject(Object object) {
        Collection /*<String>*/ result = new ArrayList();
        Collection/*<String>*/ specificationFields = ReflectionUtil.getAllFieldsNamesOfType(object.getClass(),
                Specification.class);
        for (Iterator specificationFieldsIterator = specificationFields.iterator();
             specificationFieldsIterator.hasNext(); ) {
            String field = (String) specificationFieldsIterator.next();
            Specification specification = (Specification) ReflectionUtil.invokeGetter(object, field);
            if (specification != null) {
                Collection terms = specification.getTerms();
                for (Iterator termsIterator = terms.iterator(); termsIterator.hasNext(); ) {
                    String term = ReflectionUtil.getSimpleClassName((Class) termsIterator.next());
                    result.add(term);
                }
            }

        }
        return result;
    }

    public static Collection/*<String>*/ getSpecificationClassesNamesForObject(Class clazz)
            throws IllegalAccessException, InstantiationException {
        return getSpecificationClassesNamesForObject(clazz.newInstance());
    }

    public static Collection/*<String>*/ getSpecificationClassesNamesForObject(String clazzName)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        return getSpecificationClassesNamesForObject(Class.forName(clazzName).newInstance());
    }

}

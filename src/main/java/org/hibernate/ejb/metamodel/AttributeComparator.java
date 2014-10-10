package org.hibernate.ejb.metamodel;

import java.util.Comparator;

import javax.persistence.metamodel.Attribute;

public class AttributeComparator implements Comparator<Attribute<?, ?>> {

	@Override
	public int compare(Attribute<?, ?> o1, Attribute<?, ?> o2) {
		AbstractAttribute _o1 = (AbstractAttribute) o1;
		AbstractAttribute _o2 = (AbstractAttribute) o2;
		if (_o1.getWeight() == null || _o1.getWeight() < 0
				|| _o2.getWeight() == null || _o2.getWeight() < 0) {
			return o1.getName().compareTo(o2.getName());
		} else {
			return _o2.getWeight() - _o1.getWeight();
		}
	}

}

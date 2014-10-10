/*
 * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.ejb.metamodel;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.lang3.StringUtils;

import my.com.myriadeas.integral.scaffolding.jpa.DomainAttributeSequences;

/**
 * Defines the Hibernate implementation of the JPA {@link EntityType} contract.
 * 
 * @author Steve Ebersole
 * @author Emmanuel Bernard
 */
public class EntityTypeImpl<X> extends AbstractIdentifiableType<X> implements
		EntityType<X>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7848529121796408380L;
	private final String jpaEntityName;

	public EntityTypeImpl(Class<X> javaType,
			AbstractIdentifiableType<? super X> superType,
			String jpaEntityName, boolean hasIdentifierProperty,
			boolean isVersioned) {
		super(javaType, superType, hasIdentifierProperty, isVersioned);
		this.jpaEntityName = jpaEntityName;
	}

	public String getName() {
		return StringUtils.uncapitalize(jpaEntityName);
	}

	public BindableType getBindableType() {
		return BindableType.ENTITY_TYPE;
	}

	public Class<X> getBindableJavaType() {
		return getJavaType();
	}

	public PersistenceType getPersistenceType() {
		return PersistenceType.ENTITY;
	}

	public Set<Attribute<? super X, ?>> getSortedAttributes() {
		Set<Attribute<? super X, ?>> sortedAttributes = new TreeSet<Attribute<? super X, ?>>(
				new AttributeComparator());
		for (Attribute<? super X, ?> attribute : this.getAttributes()) {
			((AbstractAttribute) attribute).setWeight(DomainAttributeSequences
					.getWeight(this.getJavaType().getSimpleName(),
							attribute.getName()));

			sortedAttributes.add(attribute);
		}
		return sortedAttributes;
	}

	public String getHumanName() {
		return StringUtils.join(
				StringUtils.splitByCharacterTypeCamelCase(StringUtils.capitalize(this.getName())), ' ');
	}

	@Override
	protected boolean requiresSupertypeForNonDeclaredIdentifier() {
		return true;
	}
}

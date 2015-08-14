/*
 * This file is part of Gradoop.
 *
 * Gradoop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gradoop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gradoop.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gradoop.model;

import java.util.Map;

/**
 * Used to describe entities that store a set of properties, where each property
 * is defined by a property key and a property value.
 */
public interface Attributed {

  /**
   * Returns all properties of that entity.
   *
   * @return properties
   */
  Map<String, Object> getProperties();

  /**
   * Returns all property keys of that entity or {@code null} it that entity has
   * no properties.
   *
   * @return property keys
   */
  Iterable<String> getPropertyKeys();

  /**
   * Returns the object referenced by the given key or {@code null} if the key
   * does not exist.
   *
   * @param key property key
   * @return property value or {@code null} if {@code key} does not exist
   */
  Object getProperty(String key);

  /**
   * Returns the value referenced by the given key or {@code null} if the key
   * does not exist.
   *
   * @param key  property key
   * @param type property value class
   * @param <T>  property type
   * @return property value or {@code null} if {@code key} does not exist
   */
  <T> T getProperty(String key, Class<T> type);

  /**
   * Sets the given properties as new properties.
   *
   * @param properties new properties
   */
  void setProperties(Map<String, Object> properties);

  /**
   * Adds a given property to that entity. If {@code key} does not exist, it
   * will be created, if it exists, the value will be overwritten by the
   * given value.
   *
   * @param key   property key
   * @param value property value
   */
  void setProperty(String key, Object value);

  /**
   * Returns the number of properties stored at that entity.
   *
   * @return number or properties
   */
  int getPropertyCount();
}

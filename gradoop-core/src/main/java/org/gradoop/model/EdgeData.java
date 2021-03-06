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

/**
 * Describes data assigned to an edge in the EPGM.
 */
public interface EdgeData extends GraphElement {
  /**
   * Returns the source vertex identifier.
   *
   * @return source vertex id
   */
  Long getSourceVertexId();

  /**
   * Sets the source vertex identifier.
   *
   * @param sourceVertexId source vertex id
   */
  void setSourceVertexId(Long sourceVertexId);

  /**
   * Returns the target vertex identifier.
   *
   * @return target vertex id
   */
  Long getTargetVertexId();

  /**
   * Sets the target vertex identifier.
   *
   * @param targetVertexId target vertex id.
   */
  void setTargetVertexId(Long targetVertexId);
}

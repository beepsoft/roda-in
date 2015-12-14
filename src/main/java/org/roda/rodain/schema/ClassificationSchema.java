package org.roda.rodain.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andre Pereira apereira@keep.pt
 * @since 07-12-2015.
 */
public class ClassificationSchema {
  private List<DescriptionObject> dos = new ArrayList<>();
  private Map<String, Object> additionalProperties = new HashMap<>();

  /**
   * Get the list of DescriptionObjects in the classification schema.
   *
   * @return The description objects list
   */
  public List<DescriptionObject> getDos() {
    return dos;
  }

  /**
   * Sets the list of DescriptionObjects.
   *
   * @param dos The description objects list
   */
  public void setDos(List<DescriptionObject> dos) {
    this.dos = dos;
  }

  /**
   * Gets the additional properties map.
   *
   * @return The additional properties map.
   */
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  /**
   * Sets an additional property.
   *
   * @param name  The name of the property.
   * @param value The value of the property.
   */
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

}

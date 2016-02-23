package org.roda.rodain.rules.sip;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import org.roda.rodain.core.AppProperties;
import org.roda.rodain.rules.MetadataTypes;
import org.roda.rodain.utils.Utils;
import org.slf4j.LoggerFactory;

/**
 * @author Andre Pereira apereira@keep.pt
 * @since 23/11/2015.
 */
public class SipMetadata {
  private static final org.slf4j.Logger log = LoggerFactory.getLogger(SipMetadata.class.getName());
  private MetadataTypes type;
  private String version;
  private String templateType;
  private boolean loaded = false, modified = false;
  private String content;
  private Path path;

  /**
   * Creates a new SipMetadata object.
   *
   * @param type         The metadata type
   * @param path         The path to the metadata file
   * @param templateType The type of the metadata template
   */
  public SipMetadata(MetadataTypes type, Path path, String templateType, String version) {
    this.type = type;
    this.path = path;
    this.templateType = templateType;
    this.version = version;
  }

  /**
   * @return True if the metadata has been modified, false otherwise.
   */
  public boolean isModified() {
    return modified;
  }

  private void loadMetadata() {
    try {
      if (type == MetadataTypes.TEMPLATE) {
        if (templateType != null) {
          content = AppProperties.getMetadataFile(templateType);
          loaded = true;
        }
      } else {
        if (path != null) {
          content = Utils.readFile(path.toString(), Charset.defaultCharset());
          loaded = true;
        }
      }
    } catch (IOException e) {
      log.error("Error reading metadata file", e);
    }
  }

  /**
   * Gets the metadata content of a SIP, loading it from the disk if that action
   * hasn't been previously done.
   *
   * @return The metadata content of the SIP.
   */
  public String getMetadataContent() {
    if (!loaded) {
      loadMetadata();
    }
    return content;
  }

  /**
   * @return The type of the metadata.
   */
  public String getTemplateType() {
    return templateType;
  }

  /**
   * @return The version of the metadata.
   */
  public String getVersion() {
    return version;
  }

  /**
   * Updates the metadata content of the SIP.
   *
   * @param meta The new metadata content.
   */
  public void update(String meta) {
    modified = true;
    content = meta;
  }
}
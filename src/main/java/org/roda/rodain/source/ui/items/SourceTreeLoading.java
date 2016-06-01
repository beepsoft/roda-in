package org.roda.rodain.source.ui.items;

import org.roda.rodain.core.I18n;

/**
 * @author Andre Pereira apereira@keep.pt
 * @since 17-09-2015.
 */
public class SourceTreeLoading extends SourceTreeItem {
  public SourceTreeLoading() {
    super(I18n.t("SourceTreeLoading.title"), null);
  }

  /**
   * @return Null because this item has no path
   */
  @Override
  public String getPath() {
    return null;
  }

  /**
   * @return The NORMAL state
   */
  @Override
  public SourceTreeItemState getState() {
    return SourceTreeItemState.NORMAL;
  }
}
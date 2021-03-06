package org.roda.rodain.ui.source;

import org.roda.rodain.core.Constants.PathState;
import org.roda.rodain.ui.source.items.SourceTreeDirectory;
import org.roda.rodain.ui.source.items.SourceTreeFile;
import org.roda.rodain.ui.source.items.SourceTreeLoadMore;
import org.roda.rodain.ui.source.items.SourceTreeLoading;

import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

/**
 * @author Andre Pereira apereira@keep.pt
 * @since 21-09-2015.
 */
public class SourceClickedEventHandler implements EventHandler<MouseEvent> {
  private TreeView<String> treeView;
  private FileExplorerPane fep;

  /**
   * Instantiates a new SourceClickedEventHandler object.
   * @param pane The application's file explorer
   */
  public SourceClickedEventHandler(FileExplorerPane pane) {
    this.treeView = pane.getTreeView();
    fep = pane;
  }

  @Override
  public void handle(MouseEvent mouseEvent) {
    if (mouseEvent.getClickCount() == 1) {
      fep.rootSelected(false);
      fep.selectedIsIgnored(false);
      TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
      if (item instanceof SourceTreeLoadMore) {
        final SourceTreeDirectory parent = (SourceTreeDirectory) item.getParent();
        parent.getChildren().remove(item);
        SourceTreeLoading loading = new SourceTreeLoading();
        parent.getChildren().add(loading);

        parent.loadMore();
      } else if (item instanceof SourceTreeDirectory) {
        SourceTreeDirectory directory = (SourceTreeDirectory) item;
        fep.updateAttributes();
        if (directory.getParentDir() == null) {
          fep.rootSelected(true);
        }
        if (directory.getState() == PathState.IGNORED) {
          fep.selectedIsIgnored(true);
        }
      } else if (item instanceof SourceTreeFile) {
        SourceTreeFile file = (SourceTreeFile) item;
        fep.updateAttributes();
        if (file.getState() == PathState.IGNORED)
          fep.selectedIsIgnored(true);
      }
    }
  }
}

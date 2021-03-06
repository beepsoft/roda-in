package org.roda.rodain.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.roda.rodain.core.schema.ClassificationSchema;
import org.roda.rodain.core.schema.Sip;
import org.roda.rodain.ui.RodaInApplication;
import org.roda.rodain.ui.creation.CreationModalPreparation;
import org.roda.rodain.ui.inspection.InspectionPane;
import org.roda.rodain.ui.schema.ui.SchemaNode;
import org.roda.rodain.ui.schema.ui.SchemaPane;
import org.roda.rodain.ui.schema.ui.SipPreviewNode;
import org.roda.rodain.ui.source.FileExplorerPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit.ApplicationTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

/**
 * @author Andre Pereira apereira@keep.pt
 * @since 17-12-2015.
 */
@Ignore
public class MainTest extends ApplicationTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainTest.class.getName());

  private static Path testDir, output;
  private SchemaPane schemaPane;
  private FileExplorerPane fileExplorer;
  private InspectionPane inspectionPane;
  private static RodaInApplication main;
  private Stage stage;

  @Override
  public void start(Stage stage) throws Exception {
    this.stage = stage;
    main = new RodaInApplication();
    main.start(stage);

    sleep(6000);

    schemaPane = RodaInApplication.getSchemePane();
    fileExplorer = RodaInApplication.getFileExplorer();
    inspectionPane = RodaInApplication.getInspectionPane();

    Path path = Paths.get("src/test/resources/plan_with_errors.json");
    InputStream stream = new FileInputStream(path.toFile());
    loadClassificationSchemeFromStream(stream, schemaPane);
  }

  /**
   * Creates a ClassificationSchema object from the InputStream and builds a
   * tree using it.
   *
   * @param stream
   *          The stream with the JSON file used to create the
   *          ClassificationSchema
   */
  private static void loadClassificationSchemeFromStream(InputStream stream, SchemaPane schemaPane) {
    try {
      schemaPane.getRootNode().getChildren().clear();
      // create ObjectMapper instance
      ObjectMapper objectMapper = new ObjectMapper();
      // convert stream to object
      ClassificationSchema scheme = objectMapper.readValue(stream, ClassificationSchema.class);
      schemaPane.updateClassificationSchema(scheme);
    } catch (IOException e) {
      LOGGER.error("Error reading classification scheme from stream", e);
    }
  }

  @Before
  public void setUpBeforeClass() throws Exception {
    testDir = Utils.createFolderStructure();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    PathCollection.reset();
    main.stop();
  }

  @Test
  public void createNewClassificationPlanWithRemovals() {
    sleep(5000);
    push(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
    sleep(3000);
    try {
      push(KeyCode.RIGHT);
      push(KeyCode.ENTER);
    } catch (Exception e) {
    }
    sleep(5000);
    clickOn(I18n.t("SchemaPane.add"));
    sleep(2000);
    clickOn(I18n.t("continue"));
    sleep(2000);
    clickOn(".schemaNode");
    sleep(2000);
    clickOn("#descObjTitle");
    eraseText(50);
    write("Node1");
    sleep(1000);

    TreeItem<String> item = RodaInApplication.getSchemePane().getTreeView().getSelectionModel().getSelectedItem();
    assert "Node1".equals(item.getValue());

    clickOn(I18n.t("SchemaPane.add"));
    sleep(500);
    clickOn(I18n.t("continue"));
    sleep(2000);
    clickOn(I18n.t("SchemaPane.newNode"));
    sleep(2000);
    clickOn("#descObjTitle");
    eraseText(50);
    write("Node2");
    sleep(1000);

    doubleClickOn(".tree-view");

    clickOn("Node2");

    TreeItem<String> newItem = RodaInApplication.getSchemePane().getTreeView().getSelectionModel().getSelectedItem();
    assert newItem instanceof SchemaNode;
    SchemaNode newNode = (SchemaNode) newItem;
    Sip dobj = newNode.getDob();
    assert dobj != null;

    sleep(2000);
    drag("Node2").dropTo(".tree-view");

    assert RodaInApplication.getSchemePane().getTreeView().getRoot().getChildren().size() == 2;
    sleep(1000);

    clickOn("Node2").clickOn("#removeLevel");
    sleep(5000);
    try {
      clickOn("OK");
    } catch (Exception e) {
      push(KeyCode.RIGHT);
      push(KeyCode.ENTER);
    }
    assert RodaInApplication.getSchemePane().getTreeView().getRoot().getChildren().size() == 1;
  }

  @Test
  public void loadAClassificationPlan() {
    sleep(5000); // wait for the classification scheme to load

    Platform.runLater(() -> {
      stage.setMaximized(false);
      stage.setMaximized(true);
    });

    clickOn("UCP");
    TreeItem selected = schemaPane.getTreeView().getSelectionModel().getSelectedItem();
    int selectedIndex = schemaPane.getTreeView().getSelectionModel().getSelectedIndex();
    assert "UCP".equals(selected.getValue());
    assert selectedIndex == 7;

    doubleClickOn(".tree-view");

    doubleClickOn("UCP");

    assert selected.getChildren().size() == 1;
  }

  @Test
  public void associateFilesAndFoldersToAnItemAndExportTheSIPs() {
    Platform.runLater(() -> {
      fileExplorer.setFileExplorerRoot(testDir);
      stage.setMaximized(false);
      stage.setMaximized(true);
    });

    sleep(5000); // wait for the tree to be created
    doubleClickOn("dir4");
    sleep(2000); // wait for the node to expand
    drag("dirB").dropTo("UCP");
    sleep(2000); // wait for the modal to open
    clickOn("#assoc3");
    clickOn("#btConfirm");
    sleep(2000); // wait for the modal to update
    clickOn("#btConfirm");
    sleep(6000); // wait for the SIPs creation

    clickOn("UCP");
    clickOn("file1.txt");
    TreeItem selected = schemaPane.getTreeView().getSelectionModel().getSelectedItem();
    assert selected instanceof SipPreviewNode;
    TreeItem parent = selected.getParent();
    assert parent instanceof SchemaNode;
    assert "UCP".equals(((SchemaNode) parent).getDob().getTitle());

    assert parent.getChildren().size() == 14;

    clickOn("UCP");
    sleep(1000);

    clickOn("#removeRule1");
    sleep(1000); // wait for the rule to be removed

    assert parent.getChildren().size() == 1;

    // create 2 SIPs
    clickOn("fileA.txt");
    press(KeyCode.CONTROL);
    clickOn("fileB.txt");
    release(KeyCode.CONTROL);

    drag().dropTo("UCP");
    sleep(2000); // wait for the modal to open
    clickOn("#assoc2");
    sleep(2000); // wait for the modal to update
    clickOn(I18n.t("continue"));
    sleep(2000); // wait for the modal to update
    clickOn("#meta4");
    clickOn(I18n.t("confirm"));
    sleep(5000); // wait for the SIPs creation

    clickOn(I18n.t("Main.file"));
    clickOn(I18n.t("Main.exportSips"));
    output = Utils.homeDir.resolve("SIPs output");
    boolean outputFolderCreated = output.toFile().mkdir();
    if (!outputFolderCreated) {
      try {
        FileUtils.cleanDirectory(output.toFile());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    CreationModalPreparation.setOutputFolder(output.toString());
    clickOn(I18n.t("start"));

    sleep(5000);
    clickOn(I18n.t("close"));

    clickOn(I18n.t("Main.file"));
    clickOn(I18n.t("Main.exportSips"));
    clickOn("#sipTypes").clickOn("BagIt");
    CreationModalPreparation.setOutputFolder(output.toString());
    clickOn(I18n.t("start"));
    sleep(5000);
    clickOn(I18n.t("close"));

    clickOn("FTP");
    sleep(1000);

    clickOn("UCP");
    clickOn("#removeRule2");
    sleep(1000); // wait for the rule to be removed
  }

  @Test
  public void associateFilesAndFoldersToAnItemAndSelectMultiple() {
    Platform.runLater(() -> {
      fileExplorer.setFileExplorerRoot(testDir);
      stage.setMaximized(false);
      sleep(500);
      stage.setMaximized(true);
    });

    sleep(5000); // wait for the tree to be created
    doubleClickOn("dir4");
    sleep(2000); // wait for the node to expand
    drag("dirB").dropTo("UCP");
    sleep(2000); // wait for the modal to open
    clickOn("#assoc3");
    clickOn("#btConfirm");
    sleep(2000); // wait for the modal to update
    clickOn("#btConfirm");
    sleep(3000); // wait for the SIPs creation

    clickOn("UCP");

    clickOn(I18n.t("SchemaPane.add"));
    sleep(2000);
    clickOn(I18n.t("continue"));
    sleep(2000);
    clickOn(I18n.t("SchemaPane.newNode"));
    clickOn("file10.txt");
    sleep(1000);

    Platform.runLater(() -> {
      schemaPane.getTreeView().getSelectionModel().selectRange(10, 15);
      inspectionPane.update(schemaPane.getTreeView().getSelectionModel().getSelectedItems());
    });

    sleep(3000);

    clickOn("#descObjTitle");
    eraseText(50);
    write("Testing");
    sleep(1000);

    sleep(1000);
    clickOn(I18n.t("apply"));
  }

  @Test
  public void create10000FilesAndCreateSIPsForEachOne() {
    Path test10000Dir = Utils.create10000Files();

    sleep(5000);
    push(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
    sleep(3000);
    try {
      push(KeyCode.RIGHT);
      push(KeyCode.ENTER);
    } catch (Exception e) {
    }

    Platform.runLater(() -> {
      fileExplorer.setFileExplorerRoot(test10000Dir);
      stage.setMaximized(false);
      sleep(500);
      stage.setMaximized(true);
      schemaPane.createClassificationScheme();

    });

    sleep(10000); // wait for the tree to be created

    drag(test10000Dir.getFileName().toString()).dropTo("#schemaPaneDropBox");

    sleep(1000); // wait for the modal to open
    clickOn("#assoc3"); // SIP per File
    clickOn(I18n.t("continue"));
    sleep(1000); // wait for the modal to update
    clickOn(I18n.t("confirm"));
    sleep(5000); // wait for the SIPs creation

    long startTimestamp = System.currentTimeMillis();
    int timeout = 120 * 1000; // 120 seconds timeout for the SIP creation
    boolean stop = false;
    while (!stop && System.currentTimeMillis() - startTimestamp < timeout) {
      try {
        clickOn(I18n.t("RuleModalProcessing.creatingPreview").toUpperCase());
        sleep(1000);
      } catch (Exception e) {
        stop = true;
        // it means that the modal has been closed and SIPs are created
      }
    }

    assert stop;
    assert RodaInApplication.getAllDescriptionObjects().size() == 10000;
  }
}

package org.roda.rodain.creation.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.roda.rodain.creation.CreateSips;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Andre Pereira apereira@keep.pt
 * @since 19/11/2015.
 */
public class CreationModalPane extends BorderPane {
    private CreateSips creator;

    // top
    private Label subtitle;
    private String subtitleFormat = "Created %d of %d";
    // center
    private ProgressBar progress;
    private Label sipName, sipAction;
    private TimerTask updater;
    private Timer timer;

    public CreationModalPane(CreateSips creator){
        this.creator = creator;

        getStyleClass().add("sipcreator");

        createTop();
        createCenter();
        createBottom();

        createUpdateTask();
    }

    private void createTop(){
        VBox top = new VBox(5);
        top.setPadding(new Insets(10, 10, 10, 0));
        top.getStyleClass().add("hbox");
        top.setAlignment(Pos.CENTER);

        Label title = new Label("Creating SIPs");
        title.setId("title");

        top.getChildren().add(title);
        setTop(top);
    }

    private void createCenter(){
        VBox center = new VBox();
        center.setAlignment(Pos.CENTER_LEFT);
        center.setPadding(new Insets(0, 10, 10, 10));

        subtitle = new Label("");
        subtitle.setId("subtitle");

        progress = new ProgressBar();
        progress.setPadding(new Insets(5,0,10,0));
        progress.setPrefSize(380, 25);

        HBox sip = new HBox(10);
        sip.maxWidth(380);
        Label lName = new Label("Current SIP:");
        lName.setMinWidth(80);
        sipName = new Label("");
        sip.getChildren().addAll(lName, sipName);

        HBox action = new HBox(10);
        Label lAction = new Label("Action:");
        lAction.setMinWidth(80);
        sipAction = new Label("");
        action.getChildren().addAll(lAction, sipAction);

        center.getChildren().addAll(subtitle, progress, sip, action);
        setCenter(center);
    }

    private void createBottom(){
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(0, 10, 10, 10));
        bottom.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel");
        // cancel timed task too

        bottom.getChildren().add(cancel);
        setBottom(bottom);
    }

    private void createUpdateTask(){
        updater = new TimerTask() {
            @Override
            public void run(){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        int created = creator.getCreatedSipsCount();
                        int size = creator.getSipsCount();

                        subtitle.setText(String.format(subtitleFormat, created, size));
                        progress.setProgress(creator.getProgress());

                        sipName.setText(creator.getSipName());
                        sipAction.setText(creator.getAction());

                        // stop the timer when all the SIPs have been created
                        if(created == size){
                            timer.cancel();
                        }
                    }
                });
            }
        };

        timer = new Timer();
        timer.schedule(updater, 0, 300);
    }
}

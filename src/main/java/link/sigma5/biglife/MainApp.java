package link.sigma5.biglife;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainApp extends Application {

    int initX=160;
    int initY=120;

    Life life= initLife();

    double mouseX;
    double mouseY;

    int shiftX=0;
    int shiftY=0;

    int permanentShiftX =0;
    int permanentShiftY =0;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext g = canvas.getGraphicsContext2D();

/*
        // Draw background
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, 800, 600);

        // Draw a shape
        g.setFill(Color.YELLOW);
        g.fillOval(100, 100, 200, 200);
*/

        // Handle mouse click
//        canvas.setOnMouseDragEntered(e -> {
//        });

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            mouseX=e.getX();
            mouseY=e.getY();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            permanentShiftX+=shiftX;
            permanentShiftY+=shiftY;
            shiftX=0;
            shiftY=0;
        });


        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            shiftX=- (int) (mouseX-e.getX());
            shiftY=- (int) (mouseY-e.getY());
        });
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            step(life);
            drawState(g, life);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Pane root = new Pane(canvas); // no layout magic here
        stage.setScene(new Scene(root));
        stage.setTitle("Big life");
        drawState(g,life);
        stage.show();
    }

    private void step(Life life) {
        life.step();
    }

    private Life initLife() {
        int countPoints = initX*initY/5;
        var random = new Random();
        Map<Life.Point, Life.Cell> cells = new HashMap<>();
        for (int i = 0; i < countPoints; i++) {
            var r=0.0;
            var g=0.0;
            var b=0.0;
            switch (random.nextInt(3)) {
                case 0 -> {
                    r=1.0;
                }
                case 1 -> {
                    g=1.0;
                }
                case 2 -> {
                    b=1.0;
                }
            }
            cells.put(new Life.Point(random.nextInt(initX), random.nextInt(initY)), new Life.Cell(r,g,b));
        }
       return new Life(cells);
    }

    public void drawState(GraphicsContext g, Life life) {
        int sizeCell=5;
        // Draw background
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, 800, 600);

        life.getRemains().forEach(p->{
            g.setFill(Color.DARKGREY);
            g.fillRect(p.x*sizeCell+ permanentShiftX +shiftX, p.y*sizeCell+ permanentShiftY +shiftY, sizeCell, sizeCell);
        });


        life.getCells().forEach((p,v)->{
            var color=new Color(v.r,v.g,v.b,1.0);
            g.setFill(color);
            g.fillRect(p.x*sizeCell+ permanentShiftX +shiftX, p.y*sizeCell+ permanentShiftY +shiftY, sizeCell, sizeCell);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
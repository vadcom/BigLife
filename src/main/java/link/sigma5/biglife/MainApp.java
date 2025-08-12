package link.sigma5.biglife;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainApp extends Application {

    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;
    double mouseX;
    double mouseY;
    int shiftX = 0;
    int shiftY = 0;
    int permanentShiftX = 0;
    int permanentShiftY = 0;
    int scale = 5;
    int initX = WINDOW_WIDTH / scale;
    int initY = WINDOW_HEIGHT / scale;
    Life life = initLife();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GraphicsContext g = canvas.getGraphicsContext2D();

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            permanentShiftX += shiftX;
            permanentShiftY += shiftY;
            shiftX = 0;
            shiftY = 0;
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            shiftX = -(int) (mouseX - e.getX());
            shiftY = -(int) (mouseY - e.getY());
        });

        canvas.setOnScroll(e -> {
            var baseWidth=WINDOW_WIDTH/(scale*2);
            var baseHeight=WINDOW_HEIGHT/(scale*2);
            System.out.println("scale:"+scale);
            System.out.println("baseWidth:"+baseWidth+" baseHeight:"+baseHeight);
            var baseShiftX=permanentShiftX/scale;
            var baseShiftY=permanentShiftY/scale;
            if (e.getDeltaY() > 0 && scale < 20) {
                scale++;
            } else if (e.getDeltaY() < 0 && scale > 1) {
                scale--;
            }
            System.out.println("New scale:"+scale);
            var newWidth=baseWidth*scale;
            var newHeight=baseHeight*scale;
            System.out.println("newWidth:"+newWidth+" newHeight:"+newHeight);
            permanentShiftX=baseShiftX*scale+WINDOW_WIDTH/2-newWidth;
            permanentShiftY=baseShiftY*scale+WINDOW_HEIGHT/2-newHeight;
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
        drawState(g, life);
        stage.show();
    }

    private void step(Life life) {
        life.step();
    }

    private Life initLife() {
        System.out.println("Init life " + "(" + initX + "," + initY + ")");
        int countPoints = (initX * initY) / 5;
        var random = new Random();
        Map<Life.Point, Life.Cell> cells = new HashMap<>();
        for (int i = 0; i < countPoints; i++) {
            var r = 0.0;
            var g = 0.0;
            var b = 0.0;
            switch (random.nextInt(3)) {
                case 0 -> {
                    r = 1.0;
                }
                case 1 -> {
                    g = 1.0;
                }
                case 2 -> {
                    b = 1.0;
                }
            }
            cells.put(new Life.Point(random.nextInt(initX), random.nextInt(initY)), new Life.Cell(r, g, b));
        }
        return new Life(cells);
    }

    public void drawState(GraphicsContext g, Life life) {
        int sizeCell = scale;
        int sizeBorder = 1;
        // Draw background
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        life.getRemains().forEach(p -> {
            g.setFill(Color.DARKGREY);
            g.fillRect(p.x * sizeCell + permanentShiftX + shiftX, p.y * sizeCell + permanentShiftY + shiftY, sizeCell, sizeCell);
        });


        life.getCells().forEach((p, v) -> {
            var color = new Color(v.r, v.g, v.b, 1.0);
            g.setFill(color);
            g.fillRect(p.x * sizeCell + permanentShiftX + shiftX, p.y * sizeCell + permanentShiftY + shiftY, sizeCell, sizeCell);
        });
    }
}
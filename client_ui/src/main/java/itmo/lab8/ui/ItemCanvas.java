package itmo.lab8.ui;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ItemCanvas extends Canvas {
    private final Long id;

    public ItemCanvas(Long id, String hex) {
        super(40, 40);
        this.id = id;
        GraphicsContext gc = this.getGraphicsContext2D();


        gc.setFill(Color.BLACK);
        gc.fillOval(0, 0, 40, 40);
        gc.setFill(Color.web("#" + hex));
        gc.fillOval(1.5, 1.5, 37, 37);
        gc.setFill(Color.BLACK);
        gc.fillOval(15, 15, 10, 10);

    }

    public Long getMovieId() {
        return id;
    }
}

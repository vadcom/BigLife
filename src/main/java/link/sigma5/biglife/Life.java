package link.sigma5.biglife;

import java.util.*;

public class Life {

    Map<Point, Boolean> cells;
    Set<Point> remains=new HashSet<>();

    public Life(Map<Point, Boolean> cells) {
        this.cells = cells;
    }

    public void step() {
        Map<Point, Boolean> born=new HashMap<>();
        Map<Point, Boolean> die=new HashMap<>();

        for (Point p : cells.keySet()) {
            int neighbours = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue;
                    Point point = new Point(p.x + i, p.y + j);
                    if (cells.containsKey(point)) {
                        neighbours++;
                    } else {
                        //
                        if (getNeighbours(point)==3) born.put(point, true);
                    }
                }
            }
            if (neighbours>3 || neighbours<2) {
                die.put(p, true);
            }
        }
        die.forEach((k,v)-> {
            cells.remove(k);
            remains.add(k);
        });
        born.forEach((k,v)->cells.put(k, true));
    }

    public Map<Point, Boolean> getCells() {
        return cells;
    }

    public Set<Point> getRemains() {
        return remains;
    }

    boolean isAlive(Point p) {
        return cells.containsKey(p);
    }

    boolean hasTrails(long x, long y) {
        return remains.contains(new Point(x,y));
    }

    private int getNeighbours(Point p) {
        int neighbours = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (cells.containsKey(new Point(p.x + i, p.y + j))) {
                    neighbours++;
                }
            }
        }
        return neighbours;
    }


    public static void main(String[] args) {
        System.out.println("Hello Life!");
        int countPoints = 1500;
        int maxPos=100;
        var random = new Random();
        Map<Point, Boolean> cells = new HashMap<>();
        for (int i = 0; i < countPoints; i++) {
            cells.put(new Point(random.nextInt(maxPos), random.nextInt(maxPos)), true);
        }
        Life life = new Life(cells);
        int generations = 1000;
        for (int i = 0; i < generations; i++) {
            long startTime = System.nanoTime();
            life.step();
            long endTime = System.nanoTime();
            System.out.println("Step calculation time: " + (endTime - startTime) / 1_000_000.0 + " ms");
            if (life.getCells().isEmpty()) {
                System.out.println("Colony died on generation " + (i+1) + " x_x");
                break;
            } else {
                System.out.println("Generation " + (i+1)+" population:"+life.getCells().size());
            }
        }
        if (!life.getCells().isEmpty()) {
            System.out.println("Colony is alive!");
            System.out.println("Population: " + life.getCells().size());
            long minX = life.getCells().keySet().stream().mapToLong(p->p.x).min().getAsLong();
            long maxX = life.getCells().keySet().stream().mapToLong(p->p.x).max().getAsLong();
            long minY = life.getCells().keySet().stream().mapToLong(p->p.y).min().getAsLong();
            long maxY = life.getCells().keySet().stream().mapToLong(p->p.y).max().getAsLong();

            long l = Math.abs(maxX - minX) + 1;
            System.out.println("Wide :" + l);
            long w = Math.abs(maxY - minY) + 1;
            System.out.println("Height: " + w);
            System.out.println("Space occupied: " +l*w);
        }

    }

    public static class Point {
        long x;
        long y;

        public Point(long x, long y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Point p) {
                return p.x == x && p.y == y;
            }
            return false;
        }
        @Override
        public int hashCode() {
            return (int) (x + y);
        }
    }
}

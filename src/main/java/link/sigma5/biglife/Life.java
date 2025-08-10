package link.sigma5.biglife;

import java.util.*;

public class Life {

    public static class Cell {
        static Random random = new Random();
        double r;
        double g;
        double b;

        double[][] genes=new double[3][3];

        public Cell(double r, double g, double b) {
            this.r = r;
            this.g = g;
            this.b = b;
            genes[0][0]=r;
            genes[0][1]=g;
            genes[0][2]=b;
            genes[1][0]=r;
            genes[1][1]=g;
            genes[1][2]=b;
            genes[2][0]=r;
            genes[2][1]=g;
            genes[2][2]=b;
        }



        public Cell(Cell[] parents){

/*
            // Dominant parents color
            this.r=(parents[0].r+parents[1].r+parents[2].r)>=2?1:0;
            this.g=(parents[0].g+parents[1].g+parents[2].g)>=2?1:0;
            this.b=(parents[0].b+parents[1].b+parents[2].b)>=2?1:0;
*/

/*
            // Medium color from parents
            this.r=(parents[0].r+parents[1].r+parents[2].r)/3;
            this.g=(parents[0].g+parents[1].g+parents[2].g)/3;
            this.b=(parents[0].b+parents[1].b+parents[2].b)/3;
*/

            // Use genes
            genes[0]=parents[0].getGenes();
            genes[1]=parents[1].getGenes();
            genes[2]=parents[2].getGenes();

            this.r=(genes[0][0]+genes[1][0]+genes[1][0])>=1?1:0;
            this.g=(genes[0][1]+genes[1][1]+genes[2][1])>=1?1:0;
            this.b=(genes[0][2]+genes[1][2]+genes[2][2])>=1?1:0;

        }

        public double[] getGenes() {
            return genes[random.nextInt(3)];
        }
    }

    Map<Point, Cell> cells;
    Set<Point> remains=new HashSet<>();

    public Life(Map<Point, Cell> cells) {
        this.cells = cells;
    }

    public void step() {
        Map<Point, Cell> born=new HashMap<>();
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
                        if (getNeighboursCount(point)==3) born.put(point, new Cell(getNeighbours(point)));
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
        cells.putAll(born);
    }

    public Map<Point, Cell> getCells() {
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

    private int getNeighboursCount(Point p) {
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

    private Cell[] getNeighbours(Point p) {
        Cell[] neighboursCell = new Cell[3];
        var neighbours=0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                Point point = new Point(p.x + i, p.y + j);
                if (cells.containsKey(point)) {
                    neighboursCell[neighbours++]=cells.get(point);
                }
            }
        }
        return neighboursCell;
    }


    public static void main(String[] args) {
        System.out.println("Hello Life!");
        int countPoints = 1500;
        int maxPos=100;
        var random = new Random();
        Map<Point, Cell> cells = new HashMap<>();
        for (int i = 0; i < countPoints; i++) {
            cells.put(new Point(random.nextInt(maxPos), random.nextInt(maxPos)), new Cell(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
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
